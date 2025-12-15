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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedListUiState>(BreedListUiState.Loading)
    val uiState: StateFlow<BreedListUiState> = _uiState.asStateFlow()

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            _uiState.value = BreedListUiState.Loading
            when (val result = breedRepository.getBreeds()) {
                is Result.Success -> _uiState.value = BreedListUiState.Success(result.data)
                is Result.Error -> _uiState.value = BreedListUiState.Error(result.exception.message)
            }
        }
    }

    fun toggleFavorite(breed: Breed) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(breed.id, breed.isFavorite)) {
                is Result.Success -> {
                    val currentState = _uiState.value
                    if (currentState is BreedListUiState.Success) {
                        val updatedBreeds = currentState.breeds.map {
                            if (it.id == breed.id) it.copy(isFavorite = result.data) else it
                        }
                        _uiState.value = BreedListUiState.Success(updatedBreeds)
                    }
                }
                is Result.Error -> { /* Optionally show error */ }
            }
        }
    }
}

sealed class BreedListUiState {
    data object Loading : BreedListUiState()
    data class Success(val breeds: List<Breed>) : BreedListUiState()
    data class Error(val message: String?) : BreedListUiState()
}
