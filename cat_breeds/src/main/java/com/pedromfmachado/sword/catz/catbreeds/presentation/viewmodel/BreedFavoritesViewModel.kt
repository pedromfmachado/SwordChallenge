package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedFavoritesViewModel @Inject constructor(
    private val breedRepository: BreedRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<BreedFavoritesUiState> = combine(
        breedRepository.observeFavoriteBreeds(),
        _isRefreshing
    ) { result, isRefreshing ->
        when (result) {
            is Result.Success -> {
                val breeds = result.data
                val averageLifespan = calculateAverageLifespan(breeds)
                BreedFavoritesUiState.Success(breeds, averageLifespan, isRefreshing)
            }
            is Result.Error -> BreedFavoritesUiState.Error(result.exception.message)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BreedFavoritesUiState.Loading
    )

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            // Refresh breed data from network (updates cached breeds)
            breedRepository.refreshBreeds()
            _isRefreshing.value = false
        }
    }

    fun toggleFavorite(breed: Breed) {
        viewModelScope.launch {
            toggleFavoriteUseCase(breed.id, breed.isFavorite)
            // No need to manually update state - the Flow will emit automatically
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
    data class Success(
        val breeds: List<Breed>,
        val averageLifespan: Int?,
        val isRefreshing: Boolean = false
    ) : BreedFavoritesUiState()
    data class Error(val message: String?) : BreedFavoritesUiState()
}
