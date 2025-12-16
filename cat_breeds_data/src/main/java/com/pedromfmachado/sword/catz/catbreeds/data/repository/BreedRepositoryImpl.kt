package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.FavoriteDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.FavoriteEntity
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedLocalMapper
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedRemoteMapper
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class BreedRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val breedDao: BreedDao,
    private val favoriteDao: FavoriteDao,
    private val breedRemoteMapper: BreedRemoteMapper,
    private val breedLocalMapper: BreedLocalMapper,
) : BreedRepository {
    override suspend fun getBreeds(
        page: Int,
        pageSize: Int,
    ): Result<List<Breed>> {
        // Network-first approach
        return try {
            val response = apiService.getBreeds(limit = pageSize, page = page)
            val breeds = breedRemoteMapper.mapToDomain(response)
            cacheBreeds(breeds)
            Result.Success(mergeFavoriteStatus(breeds))
        } catch (e: Exception) {
            // Network failed, try returning from cache
            val offset = page * pageSize
            val cachedBreeds = breedDao.getBreeds(limit = pageSize, offset = offset)
            if (cachedBreeds.isNotEmpty()) {
                Result.Success(mergeFavoriteStatus(breedLocalMapper.mapToDomain(cachedBreeds)))
            } else if (page == 0) {
                Result.Error(e)
            } else {
                // For subsequent pages, empty result is acceptable (end of cached data)
                Result.Success(emptyList())
            }
        }
    }

    override suspend fun getBreedById(id: String): Result<Breed> {
        // Use cache only (detail endpoint doesn't return image)
        val cachedBreed = breedDao.getBreedById(id)
        return if (cachedBreed != null) {
            val isFavorite = favoriteDao.isFavorite(id)
            Result.Success(breedLocalMapper.mapToDomain(cachedBreed).copy(isFavorite = isFavorite))
        } else {
            Result.Error(NoSuchElementException("Breed not found in cache"))
        }
    }

    override suspend fun getFavoriteBreeds(): Result<List<Breed>> {
        return try {
            val favoriteIds = favoriteDao.getAllFavoriteIds().first().toSet()
            val allBreeds = breedDao.getAllBreeds()
            val favorites = allBreeds
                .filter { it.id in favoriteIds }
                .let { breedLocalMapper.mapToDomain(it) }
                .map { it.copy(isFavorite = true) }
            Result.Success(favorites)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeFavoriteBreeds(): Flow<Result<List<Breed>>> {
        return favoriteDao.getAllFavoriteIds().map { favoriteIds ->
            try {
                val favoriteIdSet = favoriteIds.toSet()
                val allBreeds = breedDao.getAllBreeds()
                val favorites = allBreeds
                    .filter { it.id in favoriteIdSet }
                    .let { breedLocalMapper.mapToDomain(it) }
                    .map { it.copy(isFavorite = true) }
                Result.Success(favorites)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun observeFavoriteIds(): Flow<Set<String>> {
        return favoriteDao.getAllFavoriteIds().map { it.toSet() }
    }

    override suspend fun addFavorite(breedId: String): Result<Unit> {
        return try {
            favoriteDao.addFavorite(FavoriteEntity(breedId))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun removeFavorite(breedId: String): Result<Unit> {
        return try {
            favoriteDao.removeFavorite(breedId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun isFavorite(breedId: String): Result<Boolean> {
        return try {
            Result.Success(favoriteDao.isFavorite(breedId))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun mergeFavoriteStatus(breeds: List<Breed>): List<Breed> {
        val favoriteIds = favoriteDao.getAllFavoriteIds().first().toSet()
        return breeds.map { it.copy(isFavorite = it.id in favoriteIds) }
    }

    private suspend fun cacheBreeds(breeds: List<Breed>) {
        // REPLACE on conflict handles duplicates
        breedDao.insertBreeds(breedLocalMapper.mapToEntities(breeds))
    }
}
