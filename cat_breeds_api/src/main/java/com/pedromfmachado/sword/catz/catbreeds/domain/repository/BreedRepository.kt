package com.pedromfmachado.sword.catz.catbreeds.domain.repository

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result

interface BreedRepository {
    suspend fun getBreeds(): Result<List<Breed>>
    suspend fun getFavoriteBreeds(): Result<List<Breed>>
    suspend fun getBreedById(id: String): Result<Breed>
    suspend fun toggleFavorite(breedId: String): Result<Unit>
}
