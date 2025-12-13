# Cat API Data Source Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Replace mock data with real Cat API data fetching using Retrofit.

**Architecture:** Add networking layer to `cat_breeds_data` module with Retrofit + Moshi, update repository interface to use suspend functions with Result wrapper, update ViewModels to handle async loading states.

**Tech Stack:** Retrofit 2.11.0, Moshi 1.15.1, Kotlin Coroutines, Hilt DI

---

## Task 1: Add Dependencies

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `cat_breeds_data/build.gradle.kts`

**Step 1: Add Retrofit and Moshi to version catalog**

In `gradle/libs.versions.toml`, add after line 21 (after `hiltNavigationCompose`):

```toml
retrofit = "2.11.0"
moshi = "1.15.1"
```

In the `[libraries]` section, add after line 66 (after coil-network-okhttp):

```toml
# Retrofit
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-moshi = { group = "com.squareup.retrofit2", name = "converter-moshi", version.ref = "retrofit" }

# Moshi
moshi = { group = "com.squareup.moshi", name = "moshi", version.ref = "moshi" }
moshi-kotlin = { group = "com.squareup.moshi", name = "moshi-kotlin", version.ref = "moshi" }
```

**Step 2: Add dependencies to cat_breeds_data module**

In `cat_breeds_data/build.gradle.kts`, replace the dependencies block:

```kotlin
dependencies {
    implementation(project(":cat_breeds_api"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Testing
    testImplementation(libs.junit)
}
```

**Step 3: Verify build compiles**

Run: `./gradlew :cat_breeds_data:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add gradle/libs.versions.toml cat_breeds_data/build.gradle.kts
git commit -m "build: add Retrofit and Moshi dependencies"
```

---

## Task 2: Create Result Sealed Class

**Files:**
- Create: `cat_breeds_api/src/main/java/com/pedromfmachado/sword/catz/catbreeds/domain/result/Result.kt`

**Step 1: Create Result.kt**

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.domain.result

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}
```

**Step 2: Verify build compiles**

Run: `./gradlew :cat_breeds_api:compileKotlin`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add cat_breeds_api/src/main/java/com/pedromfmachado/sword/catz/catbreeds/domain/result/Result.kt
git commit -m "feat(api): add Result sealed class for error handling"
```

---

## Task 3: Update Repository Interface

**Files:**
- Modify: `cat_breeds_api/src/main/java/com/pedromfmachado/sword/catz/catbreeds/domain/repository/BreedRepository.kt`

**Step 1: Update BreedRepository to use suspend and Result**

Replace entire file content:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.domain.repository

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result

interface BreedRepository {
    suspend fun getBreeds(): Result<List<Breed>>
    suspend fun getFavoriteBreeds(): Result<List<Breed>>
    suspend fun getBreedById(id: String): Result<Breed>
}
```

**Step 2: Verify compilation fails (expected - implementations not updated yet)**

Run: `./gradlew :cat_breeds_data:compileDebugKotlin`
Expected: FAIL with "BreedRepositoryImpl does not implement BreedRepository"

**Step 3: Commit interface change**

```bash
git add cat_breeds_api/src/main/java/com/pedromfmachado/sword/catz/catbreeds/domain/repository/BreedRepository.kt
git commit -m "feat(api): update BreedRepository to use suspend functions with Result"
```

---

## Task 4: Create DTO Classes

**Files:**
- Create: `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/api/dto/BreedDto.kt`

**Step 1: Create BreedDto.kt**

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BreedDto(
    val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    @Json(name = "life_span") val lifeSpan: String,
    val image: ImageDto?
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    val url: String
)
```

**Step 2: Verify build compiles**

Run: `./gradlew :cat_breeds_data:compileDebugKotlin`
Expected: FAIL (still, due to BreedRepositoryImpl - expected)

**Step 3: Commit**

```bash
git add cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/api/dto/BreedDto.kt
git commit -m "feat(data): add BreedDto for Cat API response mapping"
```

---

## Task 5: Create Mapper with Tests

**Files:**
- Create: `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/mapper/BreedMapper.kt`
- Create: `cat_breeds_data/src/test/java/com/pedromfmachado/sword/catz/catbreeds/data/mapper/BreedMapperTest.kt`

**Step 1: Write the failing test**

Create test file `cat_breeds_data/src/test/java/com/pedromfmachado/sword/catz/catbreeds/data/mapper/BreedMapperTest.kt`:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.ImageDto
import org.junit.Assert.assertEquals
import org.junit.Test

class BreedMapperTest {

    private val mapper = BreedMapper()

    @Test
    fun `mapToDomain maps all fields correctly`() {
        val dto = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifeSpan = "14 - 15",
            image = ImageDto(url = "https://example.com/cat.jpg")
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals("abys", breed.id)
        assertEquals("Abyssinian", breed.name)
        assertEquals("Egypt", breed.origin)
        assertEquals("Active, Energetic", breed.temperament)
        assertEquals("The Abyssinian is easy to care for", breed.description)
        assertEquals(14, breed.lifespanLow)
        assertEquals(15, breed.lifespanHigh)
        assertEquals("https://example.com/cat.jpg", breed.imageUrl)
        assertEquals(false, breed.isFavorite)
    }

    @Test
    fun `mapToDomain handles null image`() {
        val dto = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active",
            description = "Description",
            lifeSpan = "12 - 14",
            image = null
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals("", breed.imageUrl)
    }

    @Test
    fun `parseLifespan handles single number`() {
        val dto = BreedDto(
            id = "test",
            name = "Test",
            origin = "Test",
            temperament = "Test",
            description = "Test",
            lifeSpan = "15",
            image = null
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals(15, breed.lifespanLow)
        assertEquals(15, breed.lifespanHigh)
    }

    @Test
    fun `parseLifespan handles invalid format`() {
        val dto = BreedDto(
            id = "test",
            name = "Test",
            origin = "Test",
            temperament = "Test",
            description = "Test",
            lifeSpan = "unknown",
            image = null
        )

        val breed = mapper.mapToDomain(dto)

        assertEquals(0, breed.lifespanLow)
        assertEquals(0, breed.lifespanHigh)
    }

    @Test
    fun `mapToDomain maps list correctly`() {
        val dtos = listOf(
            BreedDto("a", "A", "O", "T", "D", "10 - 12", null),
            BreedDto("b", "B", "O", "T", "D", "8 - 10", null)
        )

        val breeds = mapper.mapToDomain(dtos)

        assertEquals(2, breeds.size)
        assertEquals("a", breeds[0].id)
        assertEquals("b", breeds[1].id)
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :cat_breeds_data:testDebugUnitTest --tests "*.BreedMapperTest"`
Expected: FAIL with "Unresolved reference: BreedMapper"

**Step 3: Write minimal implementation**

Create `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/mapper/BreedMapper.kt`:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import javax.inject.Inject

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

**Step 4: Run test to verify it passes**

Run: `./gradlew :cat_breeds_data:testDebugUnitTest --tests "*.BreedMapperTest"`
Expected: PASS (5 tests)

**Step 5: Commit**

```bash
git add cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/mapper/BreedMapper.kt \
        cat_breeds_data/src/test/java/com/pedromfmachado/sword/catz/catbreeds/data/mapper/BreedMapperTest.kt
git commit -m "feat(data): add BreedMapper with unit tests"
```

---

## Task 6: Create API Service

**Files:**
- Create: `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/api/CatApiService.kt`

**Step 1: Create CatApiService.kt**

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.data.api

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApiService {

    @GET("breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 100
    ): List<BreedDto>

    @GET("breeds/{id}")
    suspend fun getBreedById(
        @Path("id") id: String
    ): BreedDto
}
```

**Step 2: Verify file compiles (module still fails due to repository)**

Run: `./gradlew :cat_breeds_data:compileDebugKotlin`
Expected: FAIL (expected - BreedRepositoryImpl not updated yet)

**Step 3: Commit**

```bash
git add cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/api/CatApiService.kt
git commit -m "feat(data): add CatApiService Retrofit interface"
```

---

## Task 7: Configure BuildConfig for API Key

**Files:**
- Modify: `cat_breeds_data/build.gradle.kts`

**Step 1: Enable BuildConfig and add API key field**

Replace entire `cat_breeds_data/build.gradle.kts`:

```kotlin
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pedromfmachado.sword.catz.catbreeds.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 30

        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val catApiKey = localProperties.getProperty("CAT_API_KEY") ?: ""
        buildConfigField("String", "CAT_API_KEY", "\"$catApiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":cat_breeds_api"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Testing
    testImplementation(libs.junit)
}
```

**Step 2: Verify BuildConfig is generated**

Run: `./gradlew :cat_breeds_data:generateDebugBuildConfig`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add cat_breeds_data/build.gradle.kts
git commit -m "build(data): configure BuildConfig for CAT_API_KEY"
```

---

## Task 8: Create Network Module

**Files:**
- Create: `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/di/NetworkModule.kt`

**Step 1: Create NetworkModule.kt**

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.data.di

import com.pedromfmachado.sword.catz.catbreeds.data.BuildConfig
import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.thecatapi.com/v1/"

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
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideCatApiService(retrofit: Retrofit): CatApiService =
        retrofit.create(CatApiService::class.java)
}
```

**Step 2: Verify file compiles**

Run: `./gradlew :cat_breeds_data:compileDebugKotlin`
Expected: FAIL (expected - BreedRepositoryImpl not updated yet)

**Step 3: Commit**

```bash
git add cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/di/NetworkModule.kt
git commit -m "feat(data): add NetworkModule for Retrofit DI"
```

---

## Task 9: Update Repository Implementation

**Files:**
- Modify: `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/repository/BreedRepositoryImpl.kt`
- Delete: `cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/mock/MockBreedData.kt` (if exists)

**Step 1: Update BreedRepositoryImpl**

Replace entire file content:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedMapper
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import javax.inject.Inject

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
        // Stub until Room is added - returns empty list
        return Result.Success(emptyList())
    }
}
```

**Step 2: Delete MockBreedData if it exists**

Run: `rm -f cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/mock/MockBreedData.kt`

**Step 3: Verify data module compiles**

Run: `./gradlew :cat_breeds_data:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/repository/BreedRepositoryImpl.kt
git rm --cached cat_breeds_data/src/main/java/com/pedromfmachado/sword/catz/catbreeds/data/mock/MockBreedData.kt 2>/dev/null || true
git commit -m "feat(data): update BreedRepositoryImpl to use Cat API"
```

---

## Task 10: Update BreedListViewModel

**Files:**
- Modify: `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/viewmodel/BreedListViewModel.kt`

**Step 1: Update BreedListViewModel with UI state**

Replace entire file content:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val breedRepository: BreedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedListUiState>(BreedListUiState.Loading)
    val uiState: StateFlow<BreedListUiState> = _uiState.asStateFlow()

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.value = BreedListUiState.Loading
            when (val result = breedRepository.getBreeds()) {
                is Result.Success -> _uiState.value = BreedListUiState.Success(result.data)
                is Result.Error -> _uiState.value = BreedListUiState.Error(result.exception.message)
            }
        }
    }
}

sealed class BreedListUiState {
    data object Loading : BreedListUiState()
    data class Success(val breeds: List<Breed>) : BreedListUiState()
    data class Error(val message: String?) : BreedListUiState()
}
```

**Step 2: Verify compiles**

Run: `./gradlew :cat_breeds:compileDebugKotlin`
Expected: FAIL (ListScreen not updated yet - expected)

**Step 3: Commit**

```bash
git add cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/viewmodel/BreedListViewModel.kt
git commit -m "feat(cat_breeds): update BreedListViewModel with async UI state"
```

---

## Task 11: Update ListScreen

**Files:**
- Modify: `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/ListScreen.kt`

**Step 1: Update ListScreen to handle UI states**

Replace entire file content:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListUiState
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListViewModel

@Composable
fun ListScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BreedListUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BreedListUiState.Success -> {
            BreedList(
                breeds = state.breeds,
                onBreedClick = onBreedClick,
                onFavoriteClick = { /* No action for now */ },
                modifier = modifier
            )
        }
        is BreedListUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message ?: stringResource(R.string.screen_list_error_generic))
            }
        }
    }
}
```

**Step 2: Add error string resource**

In `cat_breeds/src/main/res/values/strings.xml`, add:

```xml
<string name="screen_list_error_generic">Failed to load breeds</string>
```

In `cat_breeds/src/main/res/values-pt/strings.xml`, add:

```xml
<string name="screen_list_error_generic">Falha ao carregar ra\u00e7as</string>
```

**Step 3: Verify compiles**

Run: `./gradlew :cat_breeds:compileDebugKotlin`
Expected: BUILD SUCCESSFUL (or FAIL if other ViewModels not updated)

**Step 4: Commit**

```bash
git add cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/ListScreen.kt \
        cat_breeds/src/main/res/values/strings.xml \
        cat_breeds/src/main/res/values-pt/strings.xml
git commit -m "feat(cat_breeds): update ListScreen to handle loading and error states"
```

---

## Task 12: Update BreedDetailViewModel

**Files:**
- Modify: `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/viewmodel/BreedDetailViewModel.kt`

**Step 1: Update BreedDetailViewModel with UI state**

Replace entire file content:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val breedRepository: BreedRepository
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle[CatBreedsRoute.ARG_BREED_ID])

    private val _uiState = MutableStateFlow<BreedDetailUiState>(BreedDetailUiState.Loading)
    val uiState: StateFlow<BreedDetailUiState> = _uiState.asStateFlow()

    init {
        loadBreed()
    }

    private fun loadBreed() {
        viewModelScope.launch {
            _uiState.value = BreedDetailUiState.Loading
            when (val result = breedRepository.getBreedById(breedId)) {
                is Result.Success -> _uiState.value = BreedDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value = BreedDetailUiState.Error(result.exception.message)
            }
        }
    }
}

sealed class BreedDetailUiState {
    data object Loading : BreedDetailUiState()
    data class Success(val breed: Breed) : BreedDetailUiState()
    data class Error(val message: String?) : BreedDetailUiState()
}
```

**Step 2: Commit**

```bash
git add cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/viewmodel/BreedDetailViewModel.kt
git commit -m "feat(cat_breeds): update BreedDetailViewModel with async UI state"
```

---

## Task 13: Update DetailScreen

**Files:**
- Modify: `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/DetailScreen.kt`

**Step 1: Update DetailScreen to handle UI states**

Replace the `DetailScreen` composable function (keep `DetailScreenContent` and other functions unchanged):

Find and replace only the `DetailScreen` function (lines ~44-63):

```kotlin
@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BreedDetailUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BreedDetailUiState.Success -> {
            DetailScreenContent(
                breed = state.breed,
                onBackClick = onBackClick,
                onFavoriteClick = { /* No action for now */ },
                modifier = modifier
            )
        }
        is BreedDetailUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message ?: stringResource(R.string.screen_detail_error_generic))
            }
        }
    }
}
```

Add these imports at the top:

```kotlin
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedDetailUiState
```

**Step 2: Add error string resource**

In `cat_breeds/src/main/res/values/strings.xml`, add:

```xml
<string name="screen_detail_error_generic">Failed to load breed details</string>
```

In `cat_breeds/src/main/res/values-pt/strings.xml`, add:

```xml
<string name="screen_detail_error_generic">Falha ao carregar detalhes da ra\u00e7a</string>
```

**Step 3: Commit**

```bash
git add cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/DetailScreen.kt \
        cat_breeds/src/main/res/values/strings.xml \
        cat_breeds/src/main/res/values-pt/strings.xml
git commit -m "feat(cat_breeds): update DetailScreen to handle loading and error states"
```

---

## Task 14: Update BreedFavoritesViewModel

**Files:**
- Modify: `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/viewmodel/BreedFavoritesViewModel.kt`

**Step 1: Update BreedFavoritesViewModel with UI state**

Replace entire file content:

```kotlin
package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedFavoritesViewModel @Inject constructor(
    private val breedRepository: BreedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedFavoritesUiState>(BreedFavoritesUiState.Loading)
    val uiState: StateFlow<BreedFavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = BreedFavoritesUiState.Loading
            when (val result = breedRepository.getFavoriteBreeds()) {
                is Result.Success -> {
                    val breeds = result.data
                    val averageLifespan = breeds.takeIf { it.isNotEmpty() }
                        ?.map { (it.lifespanLow + it.lifespanHigh) / 2.0 }
                        ?.average()
                        ?.toInt()
                    _uiState.value = BreedFavoritesUiState.Success(breeds, averageLifespan)
                }
                is Result.Error -> _uiState.value = BreedFavoritesUiState.Error(result.exception.message)
            }
        }
    }
}

sealed class BreedFavoritesUiState {
    data object Loading : BreedFavoritesUiState()
    data class Success(val breeds: List<Breed>, val averageLifespan: Int?) : BreedFavoritesUiState()
    data class Error(val message: String?) : BreedFavoritesUiState()
}
```

**Step 2: Commit**

```bash
git add cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/viewmodel/BreedFavoritesViewModel.kt
git commit -m "feat(cat_breeds): update BreedFavoritesViewModel with async UI state"
```

---

## Task 15: Update FavoritesScreen

**Files:**
- Modify: `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/FavoritesScreen.kt`

**Step 1: Update FavoritesScreen to handle UI states**

Replace the `FavoritesScreen` composable function (keep `FavoritesScreenContent` unchanged):

```kotlin
@Composable
fun FavoritesScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedFavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BreedFavoritesUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BreedFavoritesUiState.Success -> {
            FavoritesScreenContent(
                favoriteBreeds = state.breeds,
                averageLifespan = state.averageLifespan,
                onBreedClick = onBreedClick,
                modifier = modifier
            )
        }
        is BreedFavoritesUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message ?: stringResource(R.string.screen_favorites_error_generic))
            }
        }
    }
}
```

Add these imports at the top:

```kotlin
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedFavoritesUiState
```

**Step 2: Add error string resource**

In `cat_breeds/src/main/res/values/strings.xml`, add:

```xml
<string name="screen_favorites_error_generic">Failed to load favorites</string>
```

In `cat_breeds/src/main/res/values-pt/strings.xml`, add:

```xml
<string name="screen_favorites_error_generic">Falha ao carregar favoritos</string>
```

**Step 3: Commit**

```bash
git add cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/FavoritesScreen.kt \
        cat_breeds/src/main/res/values/strings.xml \
        cat_breeds/src/main/res/values-pt/strings.xml
git commit -m "feat(cat_breeds): update FavoritesScreen to handle loading and error states"
```

---

## Task 16: Update README

**Files:**
- Create: `README.md` (if doesn't exist)

**Step 1: Create/Update README with API key instructions**

Create `README.md`:

```markdown
# Catz - Cat Breeds Browser

Android app for browsing and favoriting cat breeds, built as a challenge solution.

## Running this project

### API Key Setup

This app uses [The Cat API](https://thecatapi.com/) to fetch cat breed data.

1. Get a free API key from [thecatapi.com](https://thecatapi.com/signup)
2. Add the key to your `local.properties` file in the project root (create if it doesn't exist):
   ```properties
   CAT_API_KEY=your_api_key_here
   ```
3. Build and run the app

> **Note:** `local.properties` is gitignored and should not be committed.

## Tech Stack

- **Kotlin** with Coroutines
- **Jetpack Compose** (Material 3)
- **Hilt** for dependency injection
- **Retrofit** + **Moshi** for networking
- **Coil** for image loading
- **Navigation Compose** for navigation

## Architecture

Multi-module Clean Architecture with feature modules.

## Building

```bash
./gradlew assembleDebug
```

## Testing

```bash
./gradlew test
```
```

**Step 2: Commit**

```bash
git add README.md
git commit -m "docs: add README with API key setup instructions"
```

---

## Task 17: Final Build Verification

**Step 1: Run full build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 2: Run all tests**

Run: `./gradlew test`
Expected: All tests pass

**Step 3: Run lint**

Run: `./gradlew lint`
Expected: No critical errors

**Step 4: Final commit if any changes needed**

If any fixes were needed, commit them.

---

## Summary

After completing all tasks, the app will:
1. Fetch cat breeds from The Cat API
2. Display loading states while fetching
3. Handle and display errors gracefully
4. Use proper async patterns with coroutines
5. Have the mapper fully unit tested
