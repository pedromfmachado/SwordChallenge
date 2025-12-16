package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class BreedFavoritesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val favoritesFlow = MutableSharedFlow<Result<List<Breed>>>(replay = 1)
    private val repository: BreedRepository = mock {
        on { observeFavoriteBreeds() } doReturn favoritesFlow
    }
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
    private val viewModel = BreedFavoritesViewModel(repository, toggleFavoriteUseCase)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits favorites when flow emits`() = runTest {
        val favorites = listOf(aBreed(isFavorite = true), aBreed(id = "b", isFavorite = true))

        favoritesFlow.emit(Result.Success(favorites))

        val state = viewModel.uiState.first { it is BreedFavoritesUiState.Success } as BreedFavoritesUiState.Success
        assertEquals(2, state.breeds.size)
    }

    @Test
    fun `shows error when flow emits error`() = runTest {
        favoritesFlow.emit(Result.Error(RuntimeException("Database error")))

        val state = viewModel.uiState.first { it is BreedFavoritesUiState.Error } as BreedFavoritesUiState.Error
        assertEquals("Database error", state.message)
    }

    @Test
    fun `calculates average lifespan`() = runTest {
        val breeds = listOf(
            aBreed(lifespanLow = 10, lifespanHigh = 14),
            aBreed(id = "b", lifespanLow = 14, lifespanHigh = 16),
        )

        favoritesFlow.emit(Result.Success(breeds))

        val state = viewModel.uiState.first { it is BreedFavoritesUiState.Success } as BreedFavoritesUiState.Success
        assertEquals(13, state.averageLifespan) // (12 + 15) / 2 = 13
    }

    @Test
    fun `average lifespan is null for empty list`() = runTest {
        favoritesFlow.emit(Result.Success(emptyList()))

        val state = viewModel.uiState.first { it is BreedFavoritesUiState.Success } as BreedFavoritesUiState.Success
        assertNull(state.averageLifespan)
        assertTrue(state.breeds.isEmpty())
    }

    @Test
    fun `toggleFavorite calls use case`() = runTest {
        viewModel.toggleFavorite(aBreed(id = "abc", isFavorite = true))

        verify(toggleFavoriteUseCase).invoke("abc", true)
    }
}
