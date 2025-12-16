package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.test.aBreed
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetBreedsPageUseCaseTest {
    private val breedRepository = mock<BreedRepository>()
    private val useCase = GetBreedsPageUseCase(breedRepository)

    @Test
    fun `returns hasMorePages true when result size equals pageSize`() = runTest {
        val breeds = List(10) { aBreed(id = it.toString()) }
        whenever(breedRepository.getBreeds(0, 10)).thenReturn(Result.Success(breeds))

        val result = useCase(page = 0, pageSize = 10)

        assertTrue(result is Result.Success)
        val pageResult = (result as Result.Success).data
        assertEquals(10, pageResult.items.size)
        assertTrue(pageResult.hasMorePages)
    }

    @Test
    fun `returns hasMorePages false when result size less than pageSize`() = runTest {
        val breeds = List(5) { aBreed(id = it.toString()) }
        whenever(breedRepository.getBreeds(0, 10)).thenReturn(Result.Success(breeds))

        val result = useCase(page = 0, pageSize = 10)

        assertTrue(result is Result.Success)
        val pageResult = (result as Result.Success).data
        assertEquals(5, pageResult.items.size)
        assertFalse(pageResult.hasMorePages)
    }

    @Test
    fun `returns error when repository fails`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(breedRepository.getBreeds(0, 10)).thenReturn(Result.Error(exception))

        val result = useCase(page = 0, pageSize = 10)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `returns empty list with hasMorePages false for empty result`() = runTest {
        whenever(breedRepository.getBreeds(0, 10)).thenReturn(Result.Success(emptyList()))

        val result = useCase(page = 0, pageSize = 10)

        assertTrue(result is Result.Success)
        val pageResult = (result as Result.Success).data
        assertTrue(pageResult.items.isEmpty())
        assertFalse(pageResult.hasMorePages)
    }
}
