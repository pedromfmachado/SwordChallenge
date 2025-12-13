package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedFavoritesViewModel @Inject constructor(
    private val breedRepository: BreedRepository
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
                    val averageLifespan = breeds.takeIf { it.isNotEmpty() }
                        ?.map { (it.lifespanLow + it.lifespanHigh) / 2.0 }
                        ?.average()
                        ?.toInt()
                    _uiState.value = BreedFavoritesUiState.Success(breeds, averageLifespan)
                }
                is Result.Error -> _uiState.value = BreedFavoritesUiState.Error(result.exception.message)
            }
        }
    }
}

sealed class BreedFavoritesUiState {
    data object Loading : BreedFavoritesUiState()
    data class Success(val breeds: List<Breed>, val averageLifespan: Int?) : BreedFavoritesUiState()
    data class Error(val message: String?) : BreedFavoritesUiState()
}
