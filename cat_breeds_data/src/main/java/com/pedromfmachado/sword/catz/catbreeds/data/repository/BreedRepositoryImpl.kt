package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.cache.CacheConfig
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.CacheMetadataDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.FavoriteDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.CacheMetadataEntity
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.FavoriteEntity
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedEntityMapper
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedMapper
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import javax.inject.Inject

internal class BreedRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val mapper: BreedMapper,
    private val breedDao: BreedDao,
    private val cacheMetadataDao: CacheMetadataDao,
    private val entityMapper: BreedEntityMapper,
    private val favoriteDao: FavoriteDao
) : BreedRepository {

    override suspend fun getBreeds(): Result<List<Breed>> {
        // Check if cache is valid
        if (isCacheValid()) {
            val cachedBreeds = breedDao.getAllBreeds()
            if (cachedBreeds.isNotEmpty()) {
                return Result.Success(mergeFavoriteStatus(entityMapper.mapToDomain(cachedBreeds)))
            }
        }

        // Try network
        return try {
            val response = apiService.getBreeds()
            val breeds = mapper.mapToDomain(response)
            cacheBreeds(breeds)
            Result.Success(mergeFavoriteStatus(breeds))
        } catch (e: Exception) {
            // Network failed, try returning stale cache
            val cachedBreeds = breedDao.getAllBreeds()
            if (cachedBreeds.isNotEmpty()) {
                Result.Success(mergeFavoriteStatus(entityMapper.mapToDomain(cachedBreeds)))
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun getBreedById(id: String): Result<Breed> {
        // Use cache only (detail endpoint doesn't return image)
        val cachedBreed = breedDao.getBreedById(id)
        return if (cachedBreed != null) {
            val isFavorite = favoriteDao.isFavorite(id)
            Result.Success(entityMapper.mapToDomain(cachedBreed).copy(isFavorite = isFavorite))
        } else {
            Result.Error(NoSuchElementException("Breed not found in cache"))
        }
    }

    override suspend fun getFavoriteBreeds(): Result<List<Breed>> {
        return try {
            val favoriteIds = favoriteDao.getAllFavoriteIds().toSet()
            val allBreeds = breedDao.getAllBreeds()
            val favorites = allBreeds
                .filter { it.id in favoriteIds }
                .let { entityMapper.mapToDomain(it) }
                .map { it.copy(isFavorite = true) }
            Result.Success(favorites)
        } catch (e: Exception) {
            Result.Error(e)
        }
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
        val favoriteIds = favoriteDao.getAllFavoriteIds().toSet()
        return breeds.map { it.copy(isFavorite = it.id in favoriteIds) }
    }

    private suspend fun isCacheValid(): Boolean {
        val metadata = cacheMetadataDao.getCacheMetadata(CacheConfig.BREEDS_CACHE_KEY)
            ?: return false
        return System.currentTimeMillis() < metadata.expiresAt
    }

    private suspend fun cacheBreeds(breeds: List<Breed>) {
        val currentTime = System.currentTimeMillis()
        breedDao.deleteAllBreeds()
        breedDao.insertBreeds(entityMapper.mapToEntities(breeds))
        cacheMetadataDao.insertCacheMetadata(
            CacheMetadataEntity(
                cacheKey = CacheConfig.BREEDS_CACHE_KEY,
                lastFetchedAt = currentTime,
                expiresAt = currentTime + CacheConfig.CACHE_TTL_MS
            )
        )
    }
}
