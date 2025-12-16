# Catz - Cat Breeds Browser

Android app for browsing and favoriting cat breeds, built as a challenge solution.

## Features

- **Browse Breeds** - View a list of cat breeds with images, names, and origin
- **Infinite Scroll** - Breeds load in pages of 10 as you scroll
- **Search** - Filter loaded breeds by name with instant results (in-memory filtering)
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
- **Java** 21 (latest LTS)
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
- **List**: Fetches from network in pages of 10, caches as it goes, falls back to cache on network failure
- **Detail**: Cache-only (the API detail endpoint doesn't return images)
- **Favorites**: Stored in a separate table, persisted across app restarts, reactive updates via Flow

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

- **Unit tests** - ViewModel tests, Repository tests, Use Case tests, Mapper tests, Compose UI tests (Robolectric)
- **Screenshot tests** - Visual regression tests using Compose Preview Screenshot Testing

### Test Object Providers

Reusable test fixtures for creating domain and data objects:

```kotlin
// Domain layer (cat_breeds_api testFixtures)
aBreed(id = "abc", name = "Persian", isFavorite = true)

// Data layer (cat_breeds_data)
aBreedDto(lifeSpan = "12 - 15")
aBreedEntity(origin = "Egypt")
```

This reduces test boilerplate and ensures consistent defaults across all test files.

## Code Style

ktlint enforces the official Kotlin style guide.

```bash
# Check code style
./gradlew ktlintCheck

# Auto-fix style issues
./gradlew ktlintFormat
```

## CI

GitHub Actions workflows run on pull requests and main:
- **Unit Tests** - Runs all unit tests
- **Code Quality** - Runs ktlint and Android lint

## AI-Assisted Development

This project showcases AI-assisted development using [Claude Code](https://claude.com/claude-code).

### Challenge Evaluator Skill

A custom Claude Code skill was created to evaluate coding challenge submissions. Located in `.claude/skills/catz-challenge-evaluator/`, it provides:

- **Systematic evaluation** against challenge requirements
- **Seniority assessment** (Junior/Mid/Senior/Staff) with confidence rating
- **18 interview questions** for technical screening
- **AI-detection probes** to verify code ownership
- **Scoring rubric** for consistent evaluation

To run an evaluation, ask Claude Code: *"evaluate this challenge using the catz-challenge-evaluator skill"*
