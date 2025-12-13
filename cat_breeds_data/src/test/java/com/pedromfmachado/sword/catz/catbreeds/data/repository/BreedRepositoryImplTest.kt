package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.data.api.CatApiService
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.ImageDto
import com.pedromfmachado.sword.catz.catbreeds.data.mapper.BreedMapper
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BreedRepositoryImplTest {

    private val apiService = mock<CatApiService> {
        onBlocking { getBreeds() } doReturn listOf(BASE_DTO)
    }
    private val mapper = mock<BreedMapper> {
        on { mapToDomain(listOf(BASE_DTO)) } doReturn listOf(BASE_MODEL)
        on { mapToDomain(BASE_DTO) } doReturn BASE_MODEL
    }
    private val repository = BreedRepositoryImpl(apiService, mapper)

    @Test
    fun `getBreeds returns Success with mapped breeds when API succeeds`() = runTest {
        val result = repository.getBreeds()

        assertTrue(result is Result.Success)
        assertEquals(listOf(BASE_MODEL), (result as Result.Success).data)
    }

    @Test
    fun `getBreeds returns Error when API throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(apiService.getBreeds()).doSuspendableAnswer { throw exception }

        val result = repository.getBreeds()

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `getBreedById returns Success with mapped breed when API succeeds`() = runTest {
        whenever(apiService.getBreedById("abys")).thenReturn(BASE_DTO)

        val result = repository.getBreedById("abys")

        assertTrue(result is Result.Success)
        assertEquals(BASE_MODEL, (result as Result.Success).data)
    }

    @Test
    fun `getBreedById returns Error when API throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(apiService.getBreedById("abys")).doSuspendableAnswer { throw exception }

        val result = repository.getBreedById("abys")

        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `getFavoriteBreeds returns Success with empty list`() = runTest {
        val result = repository.getFavoriteBreeds()

        assertTrue(result is Result.Success)
        assertEquals(emptyList<Breed>(), (result as Result.Success).data)
    }

    companion object {
        private val BASE_DTO = BreedDto(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifeSpan = "14 - 15",
            image = ImageDto(url = "https://example.com/cat.jpg")
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
            isFavorite = false
        )
    }
}
