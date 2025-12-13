package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val breedRepository: BreedRepository
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle[CatBreedsRoute.ARG_BREED_ID])

    private val _uiState = MutableStateFlow<BreedDetailUiState>(BreedDetailUiState.Loading)
    val uiState: StateFlow<BreedDetailUiState> = _uiState.asStateFlow()

    init {
        loadBreed()
    }

    private fun loadBreed() {
        viewModelScope.launch {
            _uiState.value = BreedDetailUiState.Loading
            when (val result = breedRepository.getBreedById(breedId)) {
                is Result.Success -> _uiState.value = BreedDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value = BreedDetailUiState.Error(result.exception.message)
            }
        }
    }
}

sealed class BreedDetailUiState {
    data object Loading : BreedDetailUiState()
    data class Success(val breed: Breed) : BreedDetailUiState()
    data class Error(val message: String?) : BreedDetailUiState()
}
