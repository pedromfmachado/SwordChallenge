package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ApplyFavoriteStatusUseCase
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.FilterBreedsByNameUseCase
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.GetBreedsPageUseCase
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
class BreedListViewModel
    @Inject
    constructor(
        private val breedRepository: BreedRepository,
        private val getBreedsPage: GetBreedsPageUseCase,
        private val filterBreedsByName: FilterBreedsByNameUseCase,
        private val applyFavoriteStatus: ApplyFavoriteStatusUseCase,
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

                when (val result = getBreedsPage(page = 0, pageSize = PAGE_SIZE)) {
                    is Result.Success -> {
                        loadedBreeds.addAll(applyFavoriteStatus(result.data.items, favoriteIds))
                        hasMorePages = result.data.hasMorePages
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
                when (val result = getBreedsPage(page = currentPage, pageSize = PAGE_SIZE)) {
                    is Result.Success -> {
                        loadedBreeds.addAll(applyFavoriteStatus(result.data.items, favoriteIds))
                        hasMorePages = result.data.hasMorePages
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
            val updatedBreeds = applyFavoriteStatus(loadedBreeds, favoriteIds)
            loadedBreeds.clear()
            loadedBreeds.addAll(updatedBreeds)
        }

        private fun updateUiState() {
            val currentState = _uiState.value
            if (currentState is BreedListUiState.Loading && loadedBreeds.isEmpty()) {
                return // Still in initial loading state
            }

            val filteredBreeds = filterBreedsByName(loadedBreeds, _searchQuery.value)

            _uiState.value = BreedListUiState.Success(
                breeds = filteredBreeds,
                isLoadingMore = isLoadingMore,
                canLoadMore = hasMorePages && !isLoadingMore,
            )
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
