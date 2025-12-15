package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BreedFavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
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
        whenever(breedRepository.observeFavoriteBreeds()).thenReturn(flowOf(Result.Success(emptyList())))

        val viewModel = BreedFavoritesViewModel(breedRepository, toggleFavoriteUseCase)

        assertEquals(BreedFavoritesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState emits Success with favorites and average lifespan`() = runTest {
        val favorites = listOf(TEST_BREED.copy(isFavorite = true))
        whenever(breedRepository.observeFavoriteBreeds()).thenReturn(flowOf(Result.Success(favorites)))

        val viewModel = BreedFavoritesViewModel(breedRepository, toggleFavoriteUseCase)

        val state = viewModel.uiState.first { it !is BreedFavoritesUiState.Loading }
        assertTrue(state is BreedFavoritesUiState.Success)
        assertEquals(favorites, (state as BreedFavoritesUiState.Success).breeds)
        assertEquals(14, state.averageLifespan) // (14 + 15) / 2 = 14.5 -> 14
    }

    @Test
    fun `uiState emits Success with null average lifespan when no favorites`() = runTest {
        whenever(breedRepository.observeFavoriteBreeds()).thenReturn(flowOf(Result.Success(emptyList())))

        val viewModel = BreedFavoritesViewModel(breedRepository, toggleFavoriteUseCase)

        val state = viewModel.uiState.first { it !is BreedFavoritesUiState.Loading }
        assertTrue(state is BreedFavoritesUiState.Success)
        assertEquals(emptyList<Breed>(), (state as BreedFavoritesUiState.Success).breeds)
        assertEquals(null, state.averageLifespan)
    }

    @Test
    fun `uiState emits Error when repository returns error`() = runTest {
        val exception = RuntimeException("Database error")
        whenever(breedRepository.observeFavoriteBreeds()).thenReturn(flowOf(Result.Error(exception)))

        val viewModel = BreedFavoritesViewModel(breedRepository, toggleFavoriteUseCase)

        val state = viewModel.uiState.first { it !is BreedFavoritesUiState.Loading }
        assertTrue(state is BreedFavoritesUiState.Error)
        assertEquals("Database error", (state as BreedFavoritesUiState.Error).message)
    }

    @Test
    fun `toggleFavorite calls use case`() = runTest {
        val breed = TEST_BREED.copy(isFavorite = true)
        whenever(breedRepository.observeFavoriteBreeds()).thenReturn(flowOf(Result.Success(listOf(breed))))
        whenever(toggleFavoriteUseCase(breed.id, breed.isFavorite)).thenReturn(Result.Success(false))

        val viewModel = BreedFavoritesViewModel(breedRepository, toggleFavoriteUseCase)

        viewModel.toggleFavorite(breed)

        verify(toggleFavoriteUseCase).invoke(breed.id, breed.isFavorite)
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
