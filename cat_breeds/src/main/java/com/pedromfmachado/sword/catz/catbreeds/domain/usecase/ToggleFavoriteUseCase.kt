package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val breedRepository: BreedRepository
) {
    suspend operator fun invoke(breedId: String, currentlyFavorite: Boolean): Result<Boolean> {
        val result = if (currentlyFavorite) {
            breedRepository.removeFavorite(breedId)
        } else {
            breedRepository.addFavorite(breedId)
        }

        return when (result) {
            is Result.Success -> Result.Success(!currentlyFavorite)
            is Result.Error -> result
        }
    }
}
