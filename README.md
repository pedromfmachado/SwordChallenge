# Catz - Cat Breeds Browser

Android app for browsing and favoriting cat breeds, built as a challenge solution.

## Features

- **Browse Breeds** - View a list of cat breeds with images, names, and origin
- **Breed Details** - See detailed information including temperament, lifespan, and description
- **Favorites** - Mark breeds as favorites from any screen (list, detail, or favorites tab)
- **Offline Support** - Breeds are cached locally for offline viewing
- **Average Lifespan** - Favorites screen shows the average lifespan of your favorite breeds

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
- **Room** for offline caching
- **Coil** for image loading
- **Navigation Compose** for navigation

## Architecture

Multi-module Clean Architecture with feature modules.

### Offline Support

Breeds are cached locally using Room with a network-first strategy:
- **List**: Fetches from network if cache expired (24h TTL), falls back to cache on failure
- **Detail**: Cache-only (the API detail endpoint doesn't return images)
- **Favorites**: Stored in a separate table, persisted across cache refreshes, reactive updates via Flow

The 24h TTL was chosen due to the low variability of breed data.

### Reactive Favorites

The Favorites screen observes a Room Flow, so changes made from other screens (like toggling a favorite from the Detail screen) are automatically reflected when navigating back - no manual refresh needed.

## Building

```bash
./gradlew assembleDebug
```

## Testing

```bash
# Unit tests (includes Robolectric Compose UI tests)
./gradlew testDebugUnitTest

# Screenshot tests
./gradlew validateDebugScreenshotTest
```

### Test Types

- **Unit tests** - Repository tests, UseCase tests, Compose UI tests (Robolectric)
- **Screenshot tests** - Visual regression tests using Compose Preview Screenshot Testing

## CI

GitHub Actions workflows run on pull requests:
- **Unit Tests** - Runs all unit tests
