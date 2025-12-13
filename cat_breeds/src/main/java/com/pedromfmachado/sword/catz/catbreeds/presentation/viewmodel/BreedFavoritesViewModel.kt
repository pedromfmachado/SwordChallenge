package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.pedromfmachado.sword.catz.catbreeds.api.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.api.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BreedFavoritesViewModel @Inject constructor(
    private val breedRepository: BreedRepository
) : ViewModel() {

    val favoriteBreeds: List<Breed>
        get() = breedRepository.getFavoriteBreeds()

    val averageLifespan: Int?
        get() = favoriteBreeds.takeIf { it.isNotEmpty() }
            ?.map { it.lifespanLow }
            ?.average()
            ?.toInt()
}
