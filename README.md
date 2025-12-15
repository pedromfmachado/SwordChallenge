# Catz - Cat Breeds Browser

Android app for browsing and favoriting cat breeds, built as a challenge solution.

## Features

- **Browse Breeds** - View a paginated list of cat breeds with images, names, and origin
- **Breed Details** - See detailed information including temperament, lifespan, and description
- **Favorites** - Mark breeds as favorites from any screen (list, detail, or favorites tab)
- **Offline Support** - Breeds are cached locally for offline viewing
- **Pull-to-Refresh** - Refresh breed data on both List and Favorites screens
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

### Pagination

The List screen implements simple manual pagination (10 items per page) rather than using Paging3. This approach was chosen because:

1. **Small dataset** - The Cat API contains ~67 breeds, making Paging3's complexity unnecessary
2. **Challenge scope** - Focus on demonstrating architecture patterns over library integration
3. **Simplicity** - Manual pagination with ViewModel state is easier to understand and maintain

The API returns breeds in **alphabetical order by name**, providing consistent pagination across requests.

**Implementation:**
- `CatApiService` accepts `limit` and `page` parameters
- `BreedListViewModel` tracks current page and triggers `loadMoreBreeds()` when scrolling near the end
- `BreedList` composable detects scroll position and shows a loading indicator during page loads

### Reactive Favorites

Both List and Favorites screens observe Room Flows, so changes made from any screen are automatically reflected:
- **Favorites screen**: Observes full favorite breeds via `observeFavoriteBreeds()` Flow
- **List screen**: Observes only favorite IDs via `observeFavoriteIds()` Flow, updating loaded breeds in-place (pagination-friendly)

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
