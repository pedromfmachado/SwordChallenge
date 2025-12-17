package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BreedListUiState>(BreedListUiState.Loading)
    val uiState: StateFlow<BreedListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val loadedBreeds = mutableListOf<Breed>()
    private var currentPage = 0
    private var hasMorePages = true
    private var isLoadingMore = false
    private var favoriteIds = emptySet<String>()

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val PAGE_SIZE = 10
    }

    init {
        observeFavoriteIds()
        observeSearchQuery()
        loadInitialPage()
    }

    private fun loadInitialPage() {
        viewModelScope.launch {
            _uiState.value = BreedListUiState.Loading
            loadedBreeds.clear()
            currentPage = 0
            hasMorePages = true

            when (val result = breedRepository.getBreeds(page = 0, pageSize = PAGE_SIZE)) {
                is Result.Success -> {
                    loadedBreeds.addAll(applyFavoriteStatus(result.data))
                    hasMorePages = result.data.size == PAGE_SIZE
                    updateUiState()
                }

                is Result.Error -> {
                    _uiState.value = BreedListUiState.Error(result.exception.message)
                }
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingMore || !hasMorePages) return

        viewModelScope.launch {
            isLoadingMore = true
            updateUiState()

            currentPage++
            when (val result = breedRepository.getBreeds(page = currentPage, pageSize = PAGE_SIZE)) {
                is Result.Success -> {
                    loadedBreeds.addAll(applyFavoriteStatus(result.data))
                    hasMorePages = result.data.size == PAGE_SIZE
                }

                is Result.Error -> {
                    // Revert page increment on error, allow retry
                    currentPage--
                }
            }

            isLoadingMore = false
            updateUiState()
        }
    }

    private fun observeSearchQuery() {
        _searchQuery
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .onEach { updateUiState() }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun observeFavoriteIds() {
        breedRepository.observeFavoriteIds()
            .onEach { ids ->
                favoriteIds = ids
                updateLoadedBreedsWithFavorites()
                updateUiState()
            }
            .launchIn(viewModelScope)
    }

    private fun updateLoadedBreedsWithFavorites() {
        for (i in loadedBreeds.indices) {
            loadedBreeds[i] = loadedBreeds[i].copy(isFavorite = loadedBreeds[i].id in favoriteIds)
        }
    }

    private fun applyFavoriteStatus(breeds: List<Breed>): List<Breed> {
        return breeds.map { it.copy(isFavorite = it.id in favoriteIds) }
    }

    private fun updateUiState() {
        val currentState = _uiState.value
        if (currentState is BreedListUiState.Loading && loadedBreeds.isEmpty()) {
            return // Still in initial loading state
        }

        val query = _searchQuery.value
        val filteredBreeds = if (query.isBlank()) {
            loadedBreeds.toList()
        } else {
            loadedBreeds.filter { it.name.contains(query, ignoreCase = true) }
        }

        _uiState.value = BreedListUiState.Success(
            breeds = filteredBreeds,
            isLoadingMore = isLoadingMore,
            canLoadMore = hasMorePages && !isLoadingMore,
        )

        // Auto-load more pages when searching and results are sparse
        if (query.isNotBlank() && filteredBreeds.size < PAGE_SIZE && hasMorePages && !isLoadingMore) {
            loadNextPage()
        }
    }

    fun toggleFavorite(breed: Breed) {
        viewModelScope.launch {
            toggleFavoriteUseCase(breed.id, breed.isFavorite)
        }
    }
}

sealed class BreedListUiState {
    data object Loading : BreedListUiState()

    data class Success(
        val breeds: List<Breed>,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = true,
    ) : BreedListUiState()

    data class Error(val message: String?) : BreedListUiState()
}
