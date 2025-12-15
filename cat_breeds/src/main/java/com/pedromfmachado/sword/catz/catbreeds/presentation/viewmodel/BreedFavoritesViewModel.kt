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
class BreedFavoritesViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedFavoritesUiState>(BreedFavoritesUiState.Loading)
    val uiState: StateFlow<BreedFavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = BreedFavoritesUiState.Loading
            when (val result = breedRepository.getFavoriteBreeds()) {
                is Result.Success -> {
                    val breeds = result.data
                    val averageLifespan = calculateAverageLifespan(breeds)
                    _uiState.value = BreedFavoritesUiState.Success(breeds, averageLifespan)
                }
                is Result.Error -> _uiState.value = BreedFavoritesUiState.Error(result.exception.message)
            }
        }
    }

    fun toggleFavorite(breed: Breed) {
        viewModelScope.launch {
            when (toggleFavoriteUseCase(breed.id, breed.isFavorite)) {
                is Result.Success -> {
                    // On favorites screen, toggling always unfavorites (removes from list)
                    val currentState = _uiState.value
                    if (currentState is BreedFavoritesUiState.Success) {
                        val updatedBreeds = currentState.breeds.filter { it.id != breed.id }
                        val averageLifespan = calculateAverageLifespan(updatedBreeds)
                        _uiState.value = BreedFavoritesUiState.Success(updatedBreeds, averageLifespan)
                    }
                }
                is Result.Error -> { /* Optionally show error */ }
            }
        }
    }

    private fun calculateAverageLifespan(breeds: List<Breed>): Int? {
        return breeds.takeIf { it.isNotEmpty() }
            ?.map { (it.lifespanLow + it.lifespanHigh) / 2.0 }
            ?.average()
            ?.toInt()
    }
}

sealed class BreedFavoritesUiState {
    data object Loading : BreedFavoritesUiState()
    data class Success(val breeds: List<Breed>, val averageLifespan: Int?) : BreedFavoritesUiState()
    data class Error(val message: String?) : BreedFavoritesUiState()
}
