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
# Unit tests
./gradlew test

# Screenshot tests
./gradlew :cat_breeds:validateDebugScreenshotTest
```

## CI

GitHub Actions workflows run on pull requests:
- **Unit Tests** - Runs all unit tests
