package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ToggleFavoriteUseCaseTest {
    private val breedRepository = mock<BreedRepository>()
    private val useCase = ToggleFavoriteUseCase(breedRepository)

    @Test
    fun `invoke adds favorite and returns true when currently not favorite`() =
        runTest {
            whenever(breedRepository.addFavorite("abys")).thenReturn(Result.Success(Unit))

            val result = useCase("abys", currentlyFavorite = false)

            assertTrue(result is Result.Success)
            assertEquals(true, (result as Result.Success).data)
            verify(breedRepository).addFavorite("abys")
        }

    @Test
    fun `invoke removes favorite and returns false when currently favorite`() =
        runTest {
            whenever(breedRepository.removeFavorite("abys")).thenReturn(Result.Success(Unit))

            val result = useCase("abys", currentlyFavorite = true)

            assertTrue(result is Result.Success)
            assertEquals(false, (result as Result.Success).data)
            verify(breedRepository).removeFavorite("abys")
        }

    @Test
    fun `invoke returns error when repository fails`() =
        runTest {
            val exception = RuntimeException("Database error")
            whenever(breedRepository.addFavorite("abys")).thenReturn(Result.Error(exception))

            val result = useCase("abys", currentlyFavorite = false)

            assertTrue(result is Result.Error)
        }
}
