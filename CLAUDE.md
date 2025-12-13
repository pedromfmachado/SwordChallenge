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
- `app/` - Main application module (navigation shell, entry points)
  - `core/` - Application, MainActivity
  - `presentation/ui/` - MainScreen (root NavHost), TabsScreen (nested NavHost with bottom nav)
- `cat_breeds/` - Feature module for cat breed screens
  - `domain/model/` - Breed data class (id, name, imageUrl, origin, temperament, description, isFavorite)
  - `data/mock/` - MockBreedData with sample breeds
  - `presentation/navigation/` - CatBreedsRoutes (List, Favorites, Detail)
  - `presentation/ui/` - ListScreen, FavoritesScreen, DetailScreen
  - `presentation/ui/components/` - Reusable UI components (BreedList, BreedListItem)

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
- **Package**: `com.pedromfmachado.sword.catz` (app), `com.pedromfmachado.sword.catz.catbreeds` (cat_breeds)
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

- `app/build.gradle.kts` - App config
- `cat_breeds/build.gradle.kts` - Feature module config
- `gradle/libs.versions.toml` - Dependencies
- `app/src/main/java/com/pedromfmachado/sword/catz/core/` - Entry points
- `app/src/main/java/com/pedromfmachado/sword/catz/presentation/ui/MainScreen.kt` - Root NavHost (tabs + detail routes)
- `app/src/main/java/com/pedromfmachado/sword/catz/presentation/ui/TabsScreen.kt` - Nested NavHost with bottom navigation
- `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/domain/model/Breed.kt` - Breed model
- `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/navigation/CatBreedsRoutes.kt` - Navigation routes
- `cat_breeds/src/main/java/com/pedromfmachado/sword/catz/catbreeds/presentation/ui/` - Feature screens (List, Favorites, Detail)
