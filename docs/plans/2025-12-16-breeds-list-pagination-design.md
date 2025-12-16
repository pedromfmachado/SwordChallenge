# Breeds List Pagination Design

## Overview

Implement pagination for the breeds list with a page size of 10. Filtering is performed in-memory on loaded data and does not affect the loading of additional pages.

## Requirements

- Page limit: 10 breeds per page
- Filtering: In-memory on loaded breeds (not API-driven)
- Ordering: Alphabetic by name (Cat API default)
- Filter does not block loading more pages; applied to all loaded data

## Architecture Decisions

### Caching Strategy: Network-first, Cache as Offline Fallback

- Pagination always fetches from the network
- Breeds are cached as they're fetched (for offline fallback)
- Cache is only used when network fails
- Simpler implementation, fewer edge cases than smart cache pagination

### In-Memory Filtering

- ViewModel maintains all loaded breeds in memory
- Filter is applied via `List.filter {}` in ViewModel
- No Room queries for filtering (removes `searchBreeds()` dependency for list screen)
- Debounce of 300ms on search input

## Implementation Details

### API Layer

**CatApiService.kt**
```kotlin
@GET("breeds")
suspend fun getBreeds(
    @Query("limit") limit: Int = 10,
    @Query("page") page: Int = 0,
): List<BreedDto>
```

### Repository Layer

- `getBreeds(page: Int, pageSize: Int): Result<List<Breed>>` — fetches specific page from API
- Caches fetched breeds (append to existing cache)
- On network failure, attempts to serve from stale cache
- Page 0 with stale cache: clear cache before fetching

### ViewModel Layer

**State:**
```kotlin
private val loadedBreeds = mutableListOf<Breed>()
private var currentPage = 0
private var hasMorePages = true
```

**UI State:**
```kotlin
data class Success(
    val breeds: List<Breed>,      // filtered breeds to display
    val isLoadingMore: Boolean,   // loading next page indicator
    val canLoadMore: Boolean      // hasMorePages && !isLoadingMore
)
```

**Operations:**
- `loadInitialPage()` — clears state, fetches page 0
- `loadNextPage()` — fetches next page, appends to loadedBreeds, re-applies filter
- `onSearchQueryChange(query)` — updates filter, re-applies to loadedBreeds (no network)

**Filtering:**
```kotlin
loadedBreeds.filter { it.name.contains(query, ignoreCase = true) }
```

### UI Layer

**Infinite scroll detection:**
```kotlin
LaunchedEffect(listState) {
    snapshotFlow {
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val totalItems = listState.layoutInfo.totalItemsCount
        lastVisible >= totalItems - 3
    }
    .distinctUntilChanged()
    .filter { it }
    .collect { viewModel.loadNextPage() }
}
```

**Loading indicator:**
- Show spinner at bottom of list when `isLoadingMore == true`
- No indicator when `canLoadMore == false`

## Files to Modify

1. `cat_breeds_data/.../api/CatApiService.kt` — add page parameter
2. `cat_breeds_api/.../repository/BreedRepository.kt` — update interface
3. `cat_breeds_data/.../repository/BreedRepositoryImpl.kt` — implement paginated fetch
4. `cat_breeds/.../viewmodel/BreedListViewModel.kt` — pagination state, in-memory filtering
5. `cat_breeds/.../viewmodel/BreedListUiState.kt` — add isLoadingMore, canLoadMore
6. `cat_breeds/.../ui/ListScreen.kt` — infinite scroll, loading indicator

## Testing Considerations

- Unit test ViewModel pagination logic
- Unit test in-memory filtering
- Test edge cases: empty results, network failure, end of data
- Screenshot tests for loading states

## Cleanup

Delete this plan document once implementation is complete and merged.
