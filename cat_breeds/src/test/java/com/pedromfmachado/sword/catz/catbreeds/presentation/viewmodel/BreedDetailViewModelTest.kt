package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BreedDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val breedRepository = mock<BreedRepository>()
    private val toggleFavoriteUseCase = mock<ToggleFavoriteUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSavedStateHandle(breedId: String = "abys"): SavedStateHandle {
        return SavedStateHandle(mapOf(CatBreedsRoute.ARG_BREED_ID to breedId))
    }

    @Test
    fun `initial state is Loading`() = runTest {
        whenever(breedRepository.getBreedById("abys")).thenReturn(Result.Success(TEST_BREED))

        val viewModel = BreedDetailViewModel(
            createSavedStateHandle(),
            breedRepository,
            toggleFavoriteUseCase
        )

        assertEquals(BreedDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadBreed sets Success state when repository returns breed`() = runTest {
        whenever(breedRepository.getBreedById("abys")).thenReturn(Result.Success(TEST_BREED))

        val viewModel = BreedDetailViewModel(
            createSavedStateHandle(),
            breedRepository,
            toggleFavoriteUseCase
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedDetailUiState.Success)
        assertEquals(TEST_BREED, (state as BreedDetailUiState.Success).breed)
    }

    @Test
    fun `loadBreed sets Error state when repository returns error`() = runTest {
        val exception = RuntimeException("Breed not found")
        whenever(breedRepository.getBreedById("abys")).thenReturn(Result.Error(exception))

        val viewModel = BreedDetailViewModel(
            createSavedStateHandle(),
            breedRepository,
            toggleFavoriteUseCase
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedDetailUiState.Error)
        assertEquals("Breed not found", (state as BreedDetailUiState.Error).message)
    }

    @Test
    fun `toggleFavorite updates breed favorite status on success`() = runTest {
        val breed = TEST_BREED.copy(isFavorite = false)
        whenever(breedRepository.getBreedById("abys")).thenReturn(Result.Success(breed))
        whenever(toggleFavoriteUseCase(breed.id, breed.isFavorite)).thenReturn(Result.Success(true))

        val viewModel = BreedDetailViewModel(
            createSavedStateHandle(),
            breedRepository,
            toggleFavoriteUseCase
        )
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedDetailUiState.Success)
        assertTrue((state as BreedDetailUiState.Success).breed.isFavorite)
    }

    @Test
    fun `toggleFavorite does nothing when state is not Success`() = runTest {
        val exception = RuntimeException("Breed not found")
        whenever(breedRepository.getBreedById("abys")).thenReturn(Result.Error(exception))

        val viewModel = BreedDetailViewModel(
            createSavedStateHandle(),
            breedRepository,
            toggleFavoriteUseCase
        )
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedDetailUiState.Error)
    }

    companion object {
        private val TEST_BREED = Breed(
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
