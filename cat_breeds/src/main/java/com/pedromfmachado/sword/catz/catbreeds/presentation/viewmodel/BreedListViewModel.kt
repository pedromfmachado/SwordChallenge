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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<BreedListUiState>(BreedListUiState.Loading)
        val uiState: StateFlow<BreedListUiState> = _uiState.asStateFlow()

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        companion object {
            private const val SEARCH_DEBOUNCE_MS = 300L
        }

        init {
            observeFavoriteIds()
            observeSearchQuery()
        }

        @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        private fun observeSearchQuery() {
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .onEach { _uiState.value = BreedListUiState.Loading }
                .flatMapLatest { query ->
                    flow {
                        val result = if (query.isBlank()) {
                            breedRepository.getBreeds()
                        } else {
                            breedRepository.searchBreeds(query)
                        }
                        emit(result)
                    }
                }
                .onEach { result ->
                    when (result) {
                        is Result.Success -> _uiState.value = BreedListUiState.Success(result.data)
                        is Result.Error -> _uiState.value = BreedListUiState.Error(result.exception.message)
                    }
                }
                .launchIn(viewModelScope)
        }

        fun onSearchQueryChange(query: String) {
            _searchQuery.value = query
        }

        private fun observeFavoriteIds() {
            breedRepository.observeFavoriteIds()
                .onEach { favoriteIds -> updateFavoriteStatus(favoriteIds) }
                .launchIn(viewModelScope)
        }

        private fun updateFavoriteStatus(favoriteIds: Set<String>) {
            val currentState = _uiState.value
            if (currentState is BreedListUiState.Success) {
                val updatedBreeds = currentState.breeds.map { breed ->
                    breed.copy(isFavorite = breed.id in favoriteIds)
                }
                _uiState.value = BreedListUiState.Success(updatedBreeds)
            }
        }

        fun toggleFavorite(breed: Breed) {
            viewModelScope.launch {
                toggleFavoriteUseCase(breed.id, breed.isFavorite)
                // No need to manually update state - observeFavoriteIds will handle it
            }
        }
    }

sealed class BreedListUiState {
    data object Loading : BreedListUiState()

    data class Success(val breeds: List<Breed>) : BreedListUiState()

    data class Error(val message: String?) : BreedListUiState()
}
