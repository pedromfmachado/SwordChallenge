package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedMapper
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import javax.inject.Inject

internal class BreedRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val mapper: BreedMapper
) : BreedRepository {

    override suspend fun getBreeds(): Result<List<Breed>> {
        return try {
            val response = apiService.getBreeds()
            Result.Success(mapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBreedById(id: String): Result<Breed> {
        return try {
            val response = apiService.getBreedById(id)
            Result.Success(mapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getFavoriteBreeds(): Result<List<Breed>> {
        // Stub until Room is added - returns empty list
        return Result.Success(emptyList())
    }
}
