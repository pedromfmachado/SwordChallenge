package com.pedromfmachado.sword.catz.catbreeds.domain.repository

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface BreedRepository {
    suspend fun getBreeds(): Result<List<Breed>>

    suspend fun searchBreeds(query: String): Result<List<Breed>>

    suspend fun getFavoriteBreeds(): Result<List<Breed>>

    fun observeFavoriteBreeds(): Flow<Result<List<Breed>>>

    fun observeFavoriteIds(): Flow<Set<String>>

    suspend fun getBreedById(id: String): Result<Breed>

    suspend fun addFavorite(breedId: String): Result<Unit>

    suspend fun removeFavorite(breedId: String): Result<Unit>

    suspend fun isFavorite(breedId: String): Result<Boolean>
}
