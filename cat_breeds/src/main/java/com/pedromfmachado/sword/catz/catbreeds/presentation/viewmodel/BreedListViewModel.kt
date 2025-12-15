package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedListUiState>(BreedListUiState.Loading)
    val uiState: StateFlow<BreedListUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var isLoadingMore = false

    init {
        loadBreeds()
        observeFavoriteIds()
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
            _uiState.update { currentState.copy(breeds = updatedBreeds) }
        }
    }

    fun loadBreeds() {
        viewModelScope.launch {
            currentPage = 0
            _uiState.value = BreedListUiState.Loading
            when (val result = breedRepository.getBreeds(page = 0)) {
                is Result.Success -> {
                    _uiState.value = BreedListUiState.Success(
                        breeds = result.data,
                        isLoadingMore = false,
                        hasMorePages = result.data.size >= BreedRepository.DEFAULT_PAGE_SIZE,
                        isRefreshing = false
                    )
                }
                is Result.Error -> _uiState.value = BreedListUiState.Error(result.exception.message)
            }
        }
    }

    fun loadMoreBreeds() {
        val currentState = _uiState.value
        if (currentState !is BreedListUiState.Success) return
        if (isLoadingMore || !currentState.hasMorePages) return

        isLoadingMore = true
        _uiState.update { currentState.copy(isLoadingMore = true) }

        viewModelScope.launch {
            val nextPage = currentPage + 1
            when (val result = breedRepository.getBreeds(page = nextPage)) {
                is Result.Success -> {
                    currentPage = nextPage
                    val newBreeds = currentState.breeds + result.data
                    _uiState.update {
                        currentState.copy(
                            breeds = newBreeds,
                            isLoadingMore = false,
                            hasMorePages = result.data.size >= BreedRepository.DEFAULT_PAGE_SIZE
                        )
                    }
                }
                is Result.Error -> {
                    // Keep existing data, just stop loading indicator
                    _uiState.update { currentState.copy(isLoadingMore = false) }
                }
            }
            isLoadingMore = false
        }
    }

    fun refresh() {
        val currentState = _uiState.value
        if (currentState is BreedListUiState.Success && currentState.isRefreshing) return

        viewModelScope.launch {
            if (currentState is BreedListUiState.Success) {
                _uiState.update { currentState.copy(isRefreshing = true) }
            }

            currentPage = 0
            when (val result = breedRepository.refreshBreeds()) {
                is Result.Success -> {
                    _uiState.value = BreedListUiState.Success(
                        breeds = result.data,
                        isLoadingMore = false,
                        hasMorePages = result.data.size >= BreedRepository.DEFAULT_PAGE_SIZE,
                        isRefreshing = false
                    )
                }
                is Result.Error -> {
                    if (currentState is BreedListUiState.Success) {
                        // Keep existing data on refresh failure
                        _uiState.update { currentState.copy(isRefreshing = false) }
                    } else {
                        _uiState.value = BreedListUiState.Error(result.exception.message)
                    }
                }
            }
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
    data class Success(
        val breeds: List<Breed>,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = true,
        val isRefreshing: Boolean = false
    ) : BreedListUiState()
    data class Error(val message: String?) : BreedListUiState()
}
