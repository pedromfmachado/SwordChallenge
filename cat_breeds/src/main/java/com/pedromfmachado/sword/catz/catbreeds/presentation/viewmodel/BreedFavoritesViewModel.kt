package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedFavoritesViewModel @Inject constructor(
    breedRepository: BreedRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {
    val uiState: StateFlow<BreedFavoritesUiState> = breedRepository.observeFavoriteBreeds()
        .map { result ->
            when (result) {
                is Result.Success -> {
                    val breeds = result.data
                    val averageLifespan = calculateAverageLifespan(breeds)
                    BreedFavoritesUiState.Success(breeds, averageLifespan)
                }

                is Result.Error -> BreedFavoritesUiState.Error(result.exception.message)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BreedFavoritesUiState.Loading,
        )

    fun toggleFavorite(breed: Breed) {
        viewModelScope.launch {
            toggleFavoriteUseCase(breed.id, breed.isFavorite)
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
