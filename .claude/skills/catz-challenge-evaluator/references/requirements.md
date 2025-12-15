# Catz Challenge Requirements

## The Challenge

Build an Android application using Kotlin that utilizes the Cat API (https://thecatapi.com/).

**Deliverables:**
- Complete project in a version controlled repository (public)
- Multiple commits showing thought process and progress
- README explaining strategies decided during development

## Functional Requirements

### 1. List Screen
A screen with a list of cat breeds showing:
- Cat image
- Breed name

### 2. Search Bar
The cat breeds screen should contain a search bar to filter the list by breed name.

### 3. Favorite Button
The cat breeds screen should contain a button to mark the breed as favourite.

### 4. Favorites Screen
Implement a new screen to show the breeds marked as favourites.
- Show the average lifespan of all the favourite breeds (can use either lower or higher value in the range)

### 5. Detail Screen
Implement a screen with a detailed view of a breed showing:
- Breed Name
- Origin
- Temperament
- Description
- A button to add/remove the breed from the favourites

### 6. Navigation
Use Jetpack Navigation Component for navigation between screens.

### 7. Item Click
Pressing on one of the list elements (in any screen) should open the detailed view of a breed.

## Technical Requirements

| Requirement | Description |
|-------------|-------------|
| MVVM architecture | Model-View-ViewModel pattern |
| Jetpack Compose | UI building framework |
| Unit test coverage | Automated unit tests |
| Offline functionality | Data persistence (consider using Room) |

## Bonus Points

| Bonus | Description |
|-------|-------------|
| Error Handling | Graceful error states and recovery |
| Pagination | Paginated list of cat breeds |
| Modular design | Multi-module architecture |
| Integration/E2E tests | Beyond unit tests |

## Evaluation Criteria

Submissions are evaluated on:

1. **Implementation of stated requirements** - All functional and technical requirements met
2. **Application Architecture** - Clean, maintainable structure
3. **Code quality and crash resistance** - Robust, stable code
4. **Android coding conventions** - Following platform best practices
5. **Knowledge of Android libraries/SDKs** - Appropriate use of ecosystem
6. **UI/UX** - Clean design following Human Interface Guidelines (no custom assets needed)
