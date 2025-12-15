package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
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
class BreedListViewModelTest {

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

    @Test
    fun `initial state is Loading`() = runTest {
        whenever(breedRepository.getBreeds()).thenReturn(Result.Success(emptyList()))

        val viewModel = BreedListViewModel(breedRepository, toggleFavoriteUseCase)

        assertEquals(BreedListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadBreeds sets Success state when repository returns breeds`() = runTest {
        val breeds = listOf(TEST_BREED)
        whenever(breedRepository.getBreeds()).thenReturn(Result.Success(breeds))

        val viewModel = BreedListViewModel(breedRepository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedListUiState.Success)
        assertEquals(breeds, (state as BreedListUiState.Success).breeds)
    }

    @Test
    fun `loadBreeds sets Error state when repository returns error`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(breedRepository.getBreeds()).thenReturn(Result.Error(exception))

        val viewModel = BreedListViewModel(breedRepository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedListUiState.Error)
        assertEquals("Network error", (state as BreedListUiState.Error).message)
    }

    @Test
    fun `toggleFavorite updates breed favorite status on success`() = runTest {
        val breed = TEST_BREED.copy(isFavorite = false)
        whenever(breedRepository.getBreeds()).thenReturn(Result.Success(listOf(breed)))
        whenever(toggleFavoriteUseCase(breed.id, breed.isFavorite)).thenReturn(Result.Success(true))

        val viewModel = BreedListViewModel(breedRepository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite(breed)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BreedListUiState.Success)
        val updatedBreed = (state as BreedListUiState.Success).breeds.first()
        assertTrue(updatedBreed.isFavorite)
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
