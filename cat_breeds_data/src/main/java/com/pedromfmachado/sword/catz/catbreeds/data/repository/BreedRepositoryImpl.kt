package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.cache.CacheConfig
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.CacheMetadataDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.CacheMetadataEntity
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
    private val entityMapper: BreedEntityMapper
) : BreedRepository {

    override suspend fun getBreeds(): Result<List<Breed>> {
        // Check if cache is valid
        if (isCacheValid()) {
            val cachedBreeds = breedDao.getAllBreeds()
            if (cachedBreeds.isNotEmpty()) {
                return Result.Success(entityMapper.mapToDomain(cachedBreeds))
            }
        }

        // Try network
        return try {
            val response = apiService.getBreeds()
            val breeds = mapper.mapToDomain(response)
            cacheBreeds(breeds)
            Result.Success(breeds)
        } catch (e: Exception) {
            // Network failed, try returning stale cache
            val cachedBreeds = breedDao.getAllBreeds()
            if (cachedBreeds.isNotEmpty()) {
                Result.Success(entityMapper.mapToDomain(cachedBreeds))
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun getBreedById(id: String): Result<Breed> {
        // Use cache only (detail endpoint doesn't return image)
        val cachedBreed = breedDao.getBreedById(id)
        return if (cachedBreed != null) {
            Result.Success(entityMapper.mapToDomain(cachedBreed))
        } else {
            Result.Error(NoSuchElementException("Breed not found in cache"))
        }
    }

    override suspend fun getFavoriteBreeds(): Result<List<Breed>> {
        // Stub until favorites feature is implemented
        return Result.Success(emptyList())
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
