# Cat API Data Source Design

## Overview

Implement the actual data source to fetch cat breed data from The Cat API, replacing the current mock data implementation.

## Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| HTTP Client | Retrofit with coroutines | Industry standard, OkHttp integration |
| API Key Storage | BuildConfig via `local.properties` | Secure, gitignored, standard Android approach |
| Async Pattern | Suspend functions | Simpler than Flow for one-shot calls, easy migration to Flow later |
| Error Handling | Custom `Result<T>` sealed class | Type-safe, extensible error types |
| API Response Mapping | Separate DTO + Mapper | Keeps domain model clean, decoupled from API |
| JSON Parsing | Moshi | Kotlin-friendly, better null safety than Gson |
| Pagination | Fetch all (limit=100) | Cat API has ~70 breeds, pagination deferred |
| Favorites | Stub for now | Will implement with Room in future iteration |

## Architecture

### New Files in `cat_breeds_data`

```
cat_breeds_data/
├── data/
│   ├── api/
│   │   ├── CatApiService.kt
│   │   └── dto/
│   │       └── BreedDto.kt
│   ├── mapper/
│   │   └── BreedMapper.kt
│   ├── repository/
│   │   └── BreedRepositoryImpl.kt  (updated)
│   └── di/
│       ├── BreedDataModule.kt      (existing)
│       └── NetworkModule.kt        (new)
```

### New Files in `cat_breeds_api`

```
cat_breeds_api/
└── domain/
    └── result/
        └── Result.kt
```

## Implementation Details

### Result Sealed Class

```kotlin
// cat_breeds_api/.../domain/result/Result.kt
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}
```

### Repository Interface (Updated)

```kotlin
interface BreedRepository {
    suspend fun getBreeds(): Result<List<Breed>>
    suspend fun getFavoriteBreeds(): Result<List<Breed>>
    suspend fun getBreedById(id: String): Result<Breed>
}
```

### API Service

```kotlin
interface CatApiService {
    @GET("breeds")
    suspend fun getBreeds(@Query("limit") limit: Int = 100): List<BreedDto>

    @GET("breeds/{id}")
    suspend fun getBreedById(@Path("id") id: String): BreedDto
}
```

### DTO

```kotlin
data class BreedDto(
    val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    @Json(name = "life_span") val lifeSpan: String,
    val image: ImageDto?
)

data class ImageDto(
    val url: String
)
```

### Mapper

```kotlin
class BreedMapper @Inject constructor() {

    fun mapToDomain(dto: BreedDto): Breed {
        val (low, high) = parseLifespan(dto.lifeSpan)
        return Breed(
            id = dto.id,
            name = dto.name,
            imageUrl = dto.image?.url ?: "",
            origin = dto.origin,
            temperament = dto.temperament,
            description = dto.description,
            lifespanLow = low,
            lifespanHigh = high,
            isFavorite = false
        )
    }

    fun mapToDomain(dtos: List<BreedDto>): List<Breed> =
        dtos.map { mapToDomain(it) }

    private fun parseLifespan(lifespan: String): Pair<Int, Int> {
        val numbers = Regex("\\d+").findAll(lifespan)
            .map { it.value.toInt() }
            .toList()
        return Pair(
            numbers.getOrNull(0) ?: 0,
            numbers.getOrNull(1) ?: numbers.getOrNull(0) ?: 0
        )
    }
}
```

### Network Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-api-key", BuildConfig.CAT_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideCatApiService(retrofit: Retrofit): CatApiService =
        retrofit.create(CatApiService::class.java)
}
```

### Repository Implementation

```kotlin
internal class BreedRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val mapper: BreedMapper
) : BreedRepository {

    override suspend fun getBreeds(): Result<List<Breed>> {
        return try {
            val response = apiService.getBreeds()
            Result.Success(mapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBreedById(id: String): Result<Breed> {
        return try {
            val response = apiService.getBreedById(id)
            Result.Success(mapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getFavoriteBreeds(): Result<List<Breed>> {
        return Result.Success(emptyList())  // Stub until Room is added
    }
}
```

### ViewModel Pattern

```kotlin
@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val repository: BreedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedListUiState>(BreedListUiState.Loading)
    val uiState: StateFlow<BreedListUiState> = _uiState.asStateFlow()

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.value = BreedListUiState.Loading
            when (val result = repository.getBreeds()) {
                is Result.Success -> _uiState.value = BreedListUiState.Success(result.data)
                is Result.Error -> _uiState.value = BreedListUiState.Error(result.exception.message)
            }
        }
    }
}

sealed class BreedListUiState {
    object Loading : BreedListUiState()
    data class Success(val breeds: List<Breed>) : BreedListUiState()
    data class Error(val message: String?) : BreedListUiState()
}
```

## Dependencies

Add to `gradle/libs.versions.toml`:

```toml
[versions]
retrofit = "2.11.0"
moshi = "1.15.1"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-moshi = { group = "com.squareup.retrofit2", name = "converter-moshi", version.ref = "retrofit" }
moshi = { group = "com.squareup.moshi", name = "moshi", version.ref = "moshi" }
moshi-kotlin = { group = "com.squareup.moshi", name = "moshi-kotlin", version.ref = "moshi" }
```

Add to `cat_breeds_data/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
}
```

## Configuration

### API Key Setup

Add to `local.properties` (gitignored):
```properties
CAT_API_KEY=your_api_key_here
```

Add to `cat_breeds_data/build.gradle.kts`:
```kotlin
android {
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        val catApiKey = project.findProperty("CAT_API_KEY") ?: ""
        buildConfigField("String", "CAT_API_KEY", "\"$catApiKey\"")
    }
}
```

### README Documentation

Add "Running this project" section explaining API key setup.

## Future Considerations

- **Room integration**: Add local caching and offline support
- **Favorites**: Implement with Room (local-only, no API sync)
- **Pagination**: Add Paging 3 if dataset grows
- **Error refinement**: Distinguish network vs parsing errors
- **core_network module**: Extract if other features need networking
