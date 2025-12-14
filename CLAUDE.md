# Sword Challenge (Catz App)

Android app for browsing and favoriting cats.

## Tech Stack

- **Kotlin** 2.2.21 with KSP
- **Jetpack Compose** (Material 3, BOM 2025.12.00)
- **Hilt** 2.57.2 (DI)
- **Navigation Compose** 2.9.6
- **Coil** 3.1.0 (image loading)
- **Retrofit** 2.11.0 + **Moshi** 1.15.1 (networking)
- **Room** 2.7.1 (offline caching)
- **SDK**: Min 30, Target/Compile 36

## Architecture

Multi-module Clean Architecture:

```
         cat_breeds_api (Pure Kotlin)
         ├── domain/model/Breed.kt
         └── domain/repository/BreedRepository.kt
               ↑
       ┌───────┴───────┐
       ↑               ↑
cat_breeds_data    cat_breeds
  (Android)        (Android)
       ↑               ↑
       └───────┬───────┘
               ↑
              app
```

### Module Responsibilities

- `app/` - Main application module (navigation shell, entry points, DI wiring)
  - `core/` - Application, MainActivity
  - `presentation/ui/` - MainScreen (root NavHost), TabsScreen (nested NavHost with bottom nav)
  - `di/` - BaseNetworkModule (OkHttpClient, Moshi, Retrofit)

- `cat_breeds_api/` - Pure Kotlin module with domain contracts (fastest compilation)
  - `domain/model/` - Breed data class
  - `domain/repository/` - BreedRepository interface
  - `domain/result/` - Result sealed class for error handling

- `cat_breeds_data/` - Data layer implementation
  - `data/api/` - CatApiService (Retrofit), DTOs
  - `data/local/` - Room database, DAOs, entities
  - `data/cache/` - Cache configuration (24h TTL)
  - `data/mapper/` - BreedMapper (DTO to domain), BreedEntityMapper (entity to domain)
  - `data/repository/` - BreedRepositoryImpl (network-first with cache fallback)
  - `data/di/` - Hilt modules (BreedDataModule, NetworkServiceModule, DatabaseModule)

- `cat_breeds/` - Feature module for cat breed screens
  - `presentation/navigation/` - CatBreedsRoutes (List, Favorites, Detail)
  - `presentation/ui/` - ListScreen, FavoritesScreen, DetailScreen
  - `presentation/ui/components/breed/` - BreedList, BreedListItem
  - `presentation/ui/components/common/` - LoadingContent, ErrorContent
  - `presentation/viewmodel/` - ViewModels with StateFlow and UiState sealed classes
  - `preview/` - PreviewData for Compose previews

### Navigation Structure (Nested NavHosts)
```
MainScreen (RootNavHost)
├── "tabs" -> TabsScreen (Scaffold + bottom bar)
│   └── TabsNavHost
│       ├── breeds_list -> ListScreen
│       └── breeds_favorites -> FavoritesScreen
└── breeds_detail/{breedId} -> DetailScreen (full screen)
```

### Caching Strategy

- **List screen**: Network-first with cache fallback. Fetches from API if cache expired (24h TTL), falls back to stale cache on network failure.
- **Detail screen**: Cache-only. The `/breeds/:breed_id` endpoint doesn't return images, so we rely on cached data from the list.
- **TTL**: 24 hours, chosen due to low variability of breed data.
- **Metadata**: Cache validity tracked in separate `CacheMetadataEntity` table (not per-breed timestamps).

## Commands

```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew test                                    # Unit tests
./gradlew connectedCheck                          # Instrumentation tests
./gradlew :cat_breeds:validateDebugScreenshotTest # Screenshot tests
./gradlew :cat_breeds:updateDebugScreenshotTest   # Update screenshot references

# Lint
./gradlew lint
```

## Testing

- **Unit**: JUnit 4, Mockito, TestParameterInjector, Coroutines Test
- **UI**: Espresso, Compose UI Test
- **Screenshot**: Compose Preview Screenshot Testing (experimental)
  - Test location: `cat_breeds/src/screenshotTest/`
  - Reference images: `cat_breeds/src/screenshotTestDebug/reference/`

## Conventions

- **Branches**: `feature/{issue-number}-{description}`
- **Package**: `com.pedromfmachado.sword.catz.catbreeds` (shared across modules)
- **Localization**: English (default), Portuguese (`values-pt/`)
- **Version Catalog**: `gradle/libs.versions.toml`

### String Resources

Pattern: `{feature}_{element}_{purpose}` (snake_case)

| Prefix | Usage |
|--------|-------|
| `app_` | Global app-level |
| `nav_` | Navigation elements |
| `screen_{name}_` | Screen-specific content |
| `{component}_` | Component content (e.g., `breed_`) |
| `a11y_{feature}_` | Accessibility descriptions |

## Key Files

- `gradle/libs.versions.toml` - Dependencies
- `settings.gradle.kts` - Module includes
- `app/build.gradle.kts` - App config (includes API key BuildConfig)
- `app/src/main/java/.../di/BaseNetworkModule.kt` - Base networking DI
- `cat_breeds_api/src/main/java/.../domain/model/Breed.kt` - Breed model
- `cat_breeds_api/src/main/java/.../domain/repository/BreedRepository.kt` - Repository interface
- `cat_breeds_api/src/main/java/.../domain/result/Result.kt` - Result sealed class
- `cat_breeds_data/src/main/java/.../data/api/CatApiService.kt` - Retrofit API interface
- `cat_breeds_data/src/main/java/.../data/local/CatBreedsDatabase.kt` - Room database
- `cat_breeds_data/src/main/java/.../data/local/dao/BreedDao.kt` - Breed data access
- `cat_breeds_data/src/main/java/.../data/cache/CacheConfig.kt` - Cache TTL configuration
- `cat_breeds_data/src/main/java/.../data/mapper/BreedMapper.kt` - DTO to domain mapper
- `cat_breeds_data/src/main/java/.../data/di/BreedDataModule.kt` - Repository DI bindings
- `cat_breeds/src/main/java/.../presentation/navigation/CatBreedsRoutes.kt` - Navigation routes
- `cat_breeds/src/main/java/.../presentation/ui/components/common/` - Shared UI components
