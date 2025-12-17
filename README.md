# Catz - Cat Breeds Browser

[![Code Quality](https://github.com/pedromfmachado/SwordChallenge/actions/workflows/code-quality.yml/badge.svg)](https://github.com/pedromfmachado/SwordChallenge/actions/workflows/code-quality.yml)
[![Unit Tests](https://github.com/pedromfmachado/SwordChallenge/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/pedromfmachado/SwordChallenge/actions/workflows/unit-tests.yml)
[![Release](https://github.com/pedromfmachado/SwordChallenge/actions/workflows/release.yml/badge.svg)](https://github.com/pedromfmachado/SwordChallenge/actions/workflows/release.yml)

An Android app for browsing and favoriting cat breeds, built as a coding challenge for SWORD Health.

## Screenshots

<!-- TODO: Add screenshots or GIF demo -->
| List | Detail | Favorites |
|------|--------|-----------|
| ![List](screenshots/list.png) | ![Detail](screenshots/detail.png) | ![Favorites](screenshots/favorites.png) |

## Implementation

- **Browse Breeds** - Paginated list of cat breeds with images, names, and origin
- **Search** - Filter breeds by name with instant, debounced results
- **Breed Details** - Detailed view with origin, temperament, lifespan, and description
- **Favorites** - Mark breeds as favorites from any screen, persisted locally
- **Average Lifespan** - Favorites screen displays the average lifespan of favorited breeds
- **Offline Support** - Breeds are cached locally for offline viewing

### Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin, Java 21 |
| UI | Jetpack Compose, Material 3 |
| Architecture | Multi-module Clean Architecture, MVVM |
| DI | Hilt |
| Networking | Retrofit, Moshi |
| Database | Room |
| Image Loading | Coil |
| Navigation | Navigation Compose |
| Testing | JUnit, Mockito, Robolectric, Compose UI Test, MockWebServer |
| CI/CD | GitHub Actions (lint, tests, releases) |

## Getting Started

### API Key Setup

This app uses [The Cat API](https://thecatapi.com/) to fetch breed data.

1. Get a free API key from [thecatapi.com](https://thecatapi.com/signup)
2. Add the key to `local.properties` in the project root:
   ```properties
   CAT_API_KEY=your_api_key_here
   ```
3. Build and run the app

> **Note:** `local.properties` is gitignored and should not be committed.

For CI builds, the API key is injected via the `CAT_API_KEY` environment variable.

### Build & Run

```bash
# Build
./gradlew assembleDebug

# Run tests
./gradlew testDebugUnitTest              # Unit tests
./gradlew validateDebugScreenshotTest    # Screenshot tests
./gradlew :app:connectedDebugAndroidTest # E2E tests (requires device)

# Code quality
./gradlew ktlintCheck                    # Check style
./gradlew ktlintFormat                   # Fix style issues
./gradlew lint                           # Android lint
```

### Releasing

Releases are created via GitHub Actions with a manual trigger:

1. Go to **Actions** > **Release** > **Run workflow**
2. Enter a version number (e.g., `1.0.0`)
3. Click **Run workflow**

The workflow builds a debug APK with the API key embedded (from GitHub Secrets) and creates a GitHub Release with the APK attached.

> **Note:** This produces a debug build for demonstration purposes. Production releases would use a signed release build.

## Development Strategy

This project was developed using **[Claude Code](https://claude.ai/download)** as an AI pair-programming assistant. The workflow leveraged:

- **Git worktrees** - Isolated feature development without stashing or branch switching
- **Iterative development** - Features implemented incrementally with continuous refinement
- **Tests written post-implementation** - Not TDD; tests were added after features were working

The AI assisted with boilerplate generation, architecture decisions, and identifying edge cases, while I maintained control over design choices and code review.

## Testing Strategy

| Type | Tool | Purpose |
|------|------|---------|
| **Unit Tests** | JUnit, Mockito, Coroutines Test | ViewModels, Repository, UseCases, Mappers |
| **Compose UI Tests** | Robolectric, Compose UI Test | Component behavior without emulator |
| **Screenshot Tests** | Compose Preview Screenshot Testing | Visual regression, component library |
| **E2E Tests** | Hilt, MockWebServer, Compose UI Test | Full user flows with controlled API |

### Why Robolectric for Compose Tests?

Compose UI tests traditionally require an emulator or device, making them slow and flaky in CI. By using Robolectric, these tests run on the JVM with near-instant execution while still exercising real Compose rendering logic.

### Screenshot Tests as Component Library

Beyond regression testing, screenshot tests serve as a **visual catalog** of UI components. Each component has reference images that document its expected appearance across states (loading, error, empty, populated).

### Test Object Providers

Reusable fixtures reduce boilerplate and ensure consistent defaults:

```kotlin
aBreed(id = "abc", name = "Persian", isFavorite = true)  // Domain
aBreedDto(lifeSpan = "12 - 15")                          // API
aBreedEntity(origin = "Egypt")                            // Database
```

## Architecture

Multi-module Clean Architecture with four modules:

```
┌─────────────────────────────────────────────────────────────┐
│                           app                               │
│              (Navigation, DI wiring, Entry point)           │
└─────────────────────────┬───────────────────────────────────┘
                          │
          ┌───────────────┴───────────────┐
          ▼                               ▼
┌─────────────────────┐       ┌─────────────────────┐
│    cat_breeds       │       │   cat_breeds_data   │
│  (Feature module)   │       │   (Data layer)      │
│  ViewModels, UI     │       │  Room, Retrofit     │
└─────────┬───────────┘       └─────────┬───────────┘
          │                             │
          └───────────────┬─────────────┘
                          ▼
              ┌─────────────────────┐
              │   cat_breeds_api    │
              │   (Pure Kotlin)     │
              │  Domain contracts   │
              └─────────────────────┘
```

### Why Pure Kotlin for Domain?

The `cat_breeds_api` module has **zero Android dependencies**. This provides:
- Fastest possible compilation (no Android plugin overhead)
- Domain models and interfaces usable from any JVM context
- Clear architectural boundary enforcement

### Offline Strategy

| Screen | Strategy | Rationale |
|--------|----------|-----------|
| List | Network-first, cache fallback | Fresh data when online, graceful degradation offline |
| Detail | Cache-only | API detail endpoint doesn't return images |
| Favorites | Local-only | Stored in separate Room table |

### Reactive Favorites

Favorites are stored in a **separate table** rather than a column on the breeds table. This design:
- Preserves favorites when the breeds cache is refreshed
- Enables reactive updates via Room Flows
- Keeps API data separate from user preferences

Both List and Favorites screens observe Room Flows, so changes from any screen are automatically reflected everywhere.

## Decision Log

| Decision | Rationale |
|----------|-----------|
| **Robolectric for Compose tests** | Enables fast, reliable UI tests on JVM without emulator overhead. CI-friendly and maintains test confidence. |
| **Screenshot testing** | Provides visual component documentation and catches unintended UI changes. Acts as a living style guide. |
| **Local-only favorites** | The Cat API only supports favoriting images (not breeds), so local storage was the pragmatic choice for breed-level favorites. |
| **Cache-only detail screen** | The `/breeds/:breed_id` endpoint doesn't return images. Using cached data from the list ensures images are available. |
| **ViewModels access Repository directly** | Most operations lack business logic beyond CRUD. Adding UseCases for simple pass-through calls would add indirection without value. `ToggleFavoriteUseCase` exists because toggling has actual logic. |
| **No TDD** | Prioritized velocity for this challenge. Tests were added after implementation to validate behavior rather than drive design. |

## Improvements

If I were to continue developing this project:

| Improvement | Why |
|-------------|-----|
| **Replace Screenshot Testing with Roborazzi** | Compose Preview Screenshot Testing is experimental and awkward to run in CI. Roborazzi leverages Robolectric (already in use) and has better tooling. |
| **Adopt Paging 3 with RemoteMediator** | Would handle network/database coordination automatically, replacing manual pagination logic. Better separation of concerns. |
| **GitHub Actions + Claude Code integration** | Configure Claude Code to respond to PR comments and implement suggested changes automatically. |
| **TDD from the start** | Writing tests first would catch design issues earlier and ensure testability is baked in, not bolted on. |

## Learnings

Things I discovered (or re-learned) during this project:

| Learning | Context |
|----------|---------|
| **Stale cache risk in detail view** | There's no mechanism to refresh cached data when viewing details. If the API data changed since the list was fetched, the detail screen shows outdated information. |
| **Configure linting early** | Adding ktlint late in development meant fixing hundreds of style issues at once. Setting it up from day one (with pre-commit hooks) would have been much smoother. |

## 🥚 Easter Egg: I Made an AI Grade Its Own Homework

This project was built with [Claude Code](https://claude.ai/download), and because I apparently have too much free time, I created a custom skill that evaluates coding challenge submissions.

Then I pointed it at this repo.

Yes, I made an AI review the code it helped write. Yes, it rated me as "Senior Developer with 90% confidence." No, I did not bribe it with extra GPU cycles.

**[See the full evaluation report](./EVALUATION_SAMPLE.md)** - complete with interview questions designed to expose whether I actually understand my own code. *Spoiler: I'm mildly terrified to answer some of them.*

### What This Actually Demonstrates

Beyond the obvious narcissism, this showcases:
- **Custom Claude Code skills** - Structured workflows with multi-phase evaluation
- **Prompt engineering** - Defining rubrics, output formats, and evaluation criteria
- **Self-awareness** - Knowing that any interviewer reading this is now 100% going to ask me those questions

The skill lives in `.claude/skills/catz-challenge-evaluator/` if you want to see how the sausage is made. Or run it yourself if you have Claude Code:

```
evaluate this challenge
```

*The AI also generated "AI-detection probes" to verify code ownership. I'm choosing to interpret this as the machine developing trust issues, which frankly is fair.*
