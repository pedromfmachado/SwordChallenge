package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.ImageDto
import com.pedromfmachado.sword.catz.catbreeds.data.cache.CacheConfig
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.CacheMetadataDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.FavoriteDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.BreedEntity
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.CacheMetadataEntity
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.FavoriteEntity
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedLocalMapper
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedRemoteMapper
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BreedRepositoryImplTest {
    private val apiService = mock<CatApiService> {
        onBlocking { getBreeds() } doReturn listOf(BASE_DTO)
    }
    private val remoteMapper = mock<BreedRemoteMapper> {
        on { mapToDomain(listOf(BASE_DTO)) } doReturn listOf(BASE_MODEL)
        on { mapToDomain(BASE_DTO) } doReturn BASE_MODEL
    }
    private val breedDao = mock<BreedDao> {
        onBlocking { getAllBreeds() } doReturn emptyList()
    }
    private val cacheMetadataDao = mock<CacheMetadataDao> {
        onBlocking { getCacheMetadata(CacheConfig.BREEDS_CACHE_KEY) } doReturn null
    }
    private val localMapper = mock<BreedLocalMapper> {
        on { mapToEntities(listOf(BASE_MODEL)) } doReturn listOf(BASE_ENTITY)
        on { mapToDomain(listOf(BASE_ENTITY)) } doReturn listOf(BASE_MODEL)
        on { mapToDomain(BASE_ENTITY) } doReturn BASE_MODEL
    }
    private val favoriteDao = mock<FavoriteDao> {
        on { getAllFavoriteIds() } doReturn flowOf(emptyList())
        onBlocking { isFavorite("abys") } doReturn false
    }
    private val repository = BreedRepositoryImpl(
        apiService,
        breedDao,
        favoriteDao,
        cacheMetadataDao,
        remoteMapper,
        localMapper,
    )

    @Test
    fun `getBreeds fetches from network when cache is invalid`() =
        runTest {
            val result = repository.getBreeds()

            assertTrue(result is Result.Success)
            assertEquals(listOf(BASE_MODEL), (result as Result.Success).data)
            verify(apiService).getBreeds()
        }

    @Test
    fun `getBreeds returns cached data when cache is valid`() =
        runTest {
            val validMetadata = CacheMetadataEntity(
                cacheKey = CacheConfig.BREEDS_CACHE_KEY,
                lastFetchedAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + CacheConfig.CACHE_TTL_MS,
            )
            whenever(cacheMetadataDao.getCacheMetadata(CacheConfig.BREEDS_CACHE_KEY)).thenReturn(validMetadata)
            whenever(breedDao.getAllBreeds()).thenReturn(listOf(BASE_ENTITY))

            val result = repository.getBreeds()

            assertTrue(result is Result.Success)
            assertEquals(listOf(BASE_MODEL), (result as Result.Success).data)
            verify(apiService, never()).getBreeds()
        }

    @Test
    fun `getBreeds returns stale cache when network fails`() =
        runTest {
            val exception = RuntimeException("Network error")
            whenever(apiService.getBreeds()).doSuspendableAnswer { throw exception }
            whenever(breedDao.getAllBreeds()).thenReturn(listOf(BASE_ENTITY))

            val result = repository.getBreeds()

            assertTrue(result is Result.Success)
            assertEquals(listOf(BASE_MODEL), (result as Result.Success).data)
        }

    @Test
    fun `getBreeds returns Error when network fails and cache is empty`() =
        runTest {
            val exception = RuntimeException("Network error")
            whenever(apiService.getBreeds()).doSuspendableAnswer { throw exception }
            whenever(breedDao.getAllBreeds()).thenReturn(emptyList())

            val result = repository.getBreeds()

            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
        }

    @Test
    fun `getBreedById returns cached breed when available`() =
        runTest {
            whenever(breedDao.getBreedById("abys")).thenReturn(BASE_ENTITY)

            val result = repository.getBreedById("abys")

            assertTrue(result is Result.Success)
            assertEquals(BASE_MODEL, (result as Result.Success).data)
        }

    @Test
    fun `getBreedById returns Error when breed not cached`() =
        runTest {
            whenever(breedDao.getBreedById("abys")).thenReturn(null)

            val result = repository.getBreedById("abys")

            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception is NoSuchElementException)
        }

    @Test
    fun `getFavoriteBreeds returns Success with empty list when no favorites`() =
        runTest {
            whenever(favoriteDao.getAllFavoriteIds()).thenReturn(flowOf(emptyList()))
            whenever(breedDao.getAllBreeds()).thenReturn(listOf(BASE_ENTITY))

            val result = repository.getFavoriteBreeds()

            assertTrue(result is Result.Success)
            assertEquals(emptyList<Breed>(), (result as Result.Success).data)
        }

    @Test
    fun `getFavoriteBreeds returns favorite breeds with isFavorite true`() =
        runTest {
            whenever(favoriteDao.getAllFavoriteIds()).thenReturn(flowOf(listOf("abys")))
            whenever(breedDao.getAllBreeds()).thenReturn(listOf(BASE_ENTITY))

            val result = repository.getFavoriteBreeds()

            assertTrue(result is Result.Success)
            val favorites = (result as Result.Success).data
            assertEquals(1, favorites.size)
            assertEquals("abys", favorites[0].id)
            assertTrue(favorites[0].isFavorite)
        }

    @Test
    fun `addFavorite adds breed to favorites`() =
        runTest {
            val result = repository.addFavorite("abys")

            assertTrue(result is Result.Success)
            verify(favoriteDao).addFavorite(FavoriteEntity("abys"))
        }

    @Test
    fun `removeFavorite removes breed from favorites`() =
        runTest {
            val result = repository.removeFavorite("abys")

            assertTrue(result is Result.Success)
            verify(favoriteDao).removeFavorite("abys")
        }

    @Test
    fun `isFavorite returns true when breed is favorited`() =
        runTest {
            whenever(favoriteDao.isFavorite("abys")).thenReturn(true)

            val result = repository.isFavorite("abys")

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data)
        }

    @Test
    fun `isFavorite returns false when breed is not favorited`() =
        runTest {
            whenever(favoriteDao.isFavorite("abys")).thenReturn(false)

            val result = repository.isFavorite("abys")

            assertTrue(result is Result.Success)
            assertEquals(false, (result as Result.Success).data)
        }

    @Test
    fun `getBreeds merges favorite status from favorites table`() =
        runTest {
            whenever(favoriteDao.getAllFavoriteIds()).thenReturn(flowOf(listOf("abys")))

            val result = repository.getBreeds()

            assertTrue(result is Result.Success)
            val breeds = (result as Result.Success).data
            assertEquals(1, breeds.size)
            assertTrue(breeds[0].isFavorite)
        }

    @Test
    fun `observeFavoriteBreeds emits favorite breeds with isFavorite true`() =
        runTest {
            whenever(favoriteDao.getAllFavoriteIds()).thenReturn(flowOf(listOf("abys")))
            whenever(breedDao.getAllBreeds()).thenReturn(listOf(BASE_ENTITY))

            val result = repository.observeFavoriteBreeds().first()

            assertTrue(result is Result.Success)
            val favorites = (result as Result.Success).data
            assertEquals(1, favorites.size)
            assertEquals("abys", favorites[0].id)
            assertTrue(favorites[0].isFavorite)
        }

    @Test
    fun `observeFavoriteIds emits set of favorite IDs`() =
        runTest {
            whenever(favoriteDao.getAllFavoriteIds()).thenReturn(flowOf(listOf("abys", "beng")))

            val result = repository.observeFavoriteIds().first()

            assertEquals(setOf("abys", "beng"), result)
        }

    @Test
    fun `getBreedById returns breed with correct favorite status`() =
        runTest {
            whenever(breedDao.getBreedById("abys")).thenReturn(BASE_ENTITY)
            whenever(favoriteDao.isFavorite("abys")).thenReturn(true)

            val result = repository.getBreedById("abys")

            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.isFavorite)
        }

    companion object {
        private val BASE_DTO = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifeSpan = "14 - 15",
            image = ImageDto(url = "https://example.com/cat.jpg"),
        )

        private val BASE_MODEL = Breed(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifespanLow = 14,
            lifespanHigh = 15,
            imageUrl = "https://example.com/cat.jpg",
            isFavorite = false,
        )

        private val BASE_ENTITY = BreedEntity(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifespanLow = 14,
            lifespanHigh = 15,
            imageUrl = "https://example.com/cat.jpg",
        )
    }
}
