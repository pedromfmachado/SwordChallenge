package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.pedromfmachado.sword.catz.catbreeds.api.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.api.repository.BreedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BreedListViewModel @Inject constructor(
    private val breedRepository: BreedRepository
) : ViewModel() {

    val breeds: List<Breed>
        get() = breedRepository.getBreeds()
}
