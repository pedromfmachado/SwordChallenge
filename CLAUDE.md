# Sword Challenge (Catz App)

Android app for browsing and favoriting cats.

## Tech Stack

- **Kotlin** 2.2.21 with KSP
- **Jetpack Compose** (Material 3, BOM 2025.12.00)
- **Hilt** 2.57.2 (DI)
- **Navigation Compose** 2.9.6
- **Coil** 3.1.0 (image loading)
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

- `cat_breeds_api/` - Pure Kotlin module with domain contracts (fastest compilation)
  - `domain/model/` - Breed data class
  - `domain/repository/` - BreedRepository interface

- `cat_breeds_data/` - Data layer implementation
  - `data/repository/` - BreedRepositoryImpl
  - `data/mock/` - MockBreedData
  - `data/di/` - Hilt module (BreedDataModule)

- `cat_breeds/` - Feature module for cat breed screens
  - `presentation/navigation/` - CatBreedsRoutes (List, Favorites, Detail)
  - `presentation/ui/` - ListScreen, FavoritesScreen, DetailScreen
  - `presentation/ui/components/` - Reusable UI components (BreedList, BreedListItem)
  - `presentation/viewmodel/` - ViewModels (BreedListViewModel, BreedFavoritesViewModel, BreedDetailViewModel)
  - `src/debug/` - Preview data (PreviewData.kt) - excluded from release builds

### Navigation Structure (Nested NavHosts)
```
MainScreen (RootNavHost)
├── "tabs" -> TabsScreen (Scaffold + bottom bar)
│   └── TabsNavHost
│       ├── breeds_list -> ListScreen
│       └── breeds_favorites -> FavoritesScreen
└── breeds_detail/{breedId} -> DetailScreen (full screen)
```

## Commands

```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew test              # Unit tests
./gradlew connectedCheck    # Instrumentation tests

# Lint
./gradlew lint
```

## Testing

- **Unit**: JUnit 4, Mockito, Robolectric
- **UI**: Espresso, Compose UI Test

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
- `app/build.gradle.kts` - App config
- `cat_breeds_api/build.gradle.kts` - API module config (pure Kotlin)
- `cat_breeds_data/build.gradle.kts` - Data module config
- `cat_breeds/build.gradle.kts` - Feature module config
- `cat_breeds_api/src/main/java/.../domain/model/Breed.kt` - Breed model
- `cat_breeds_api/src/main/java/.../domain/repository/BreedRepository.kt` - Repository interface
- `cat_breeds_data/src/main/java/.../data/di/BreedDataModule.kt` - Hilt DI bindings
- `cat_breeds/src/main/java/.../presentation/navigation/CatBreedsRoutes.kt` - Navigation routes
