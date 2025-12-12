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

Clean Architecture with:
- `core/` - App setup (Application, MainActivity)
- `presentation/ui/` - Compose screens

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
- **Package**: `com.pedromfmachado.sword`
- **Localization**: English (default), Portuguese (`values-pt/`)
- **Version Catalog**: `gradle/libs.versions.toml`

## Key Files

- `app/build.gradle.kts` - App config
- `gradle/libs.versions.toml` - Dependencies
- `app/src/main/java/com/pedromfmachado/sword/core/` - Entry points
- `app/src/main/java/com/pedromfmachado/sword/presentation/ui/` - UI components
