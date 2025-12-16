package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BreedDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val savedStateHandle = SavedStateHandle(mapOf(CatBreedsRoute.ARG_BREED_ID to "abys"))
    private val repository: BreedRepository = mock {
        onBlocking { getBreedById("abys") } doReturn Result.Success(aBreed())
    }
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads breed on init`() = runTest {
        val breed = aBreed(name = "Bengal")
        whenever(repository.getBreedById("abys")).thenReturn(Result.Success(breed))

        val viewModel = BreedDetailViewModel(savedStateHandle, repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
        assertEquals("Bengal", state.breed.name)
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        whenever(repository.getBreedById("abys")).thenReturn(Result.Error(RuntimeException("Not found")))

        val viewModel = BreedDetailViewModel(savedStateHandle, repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Error
        assertEquals("Not found", state.message)
    }

    @Test
    fun `toggleFavorite updates state and calls use case`() = runTest {
        whenever(repository.getBreedById("abys")).thenReturn(Result.Success(aBreed(isFavorite = false)))
        whenever(toggleFavoriteUseCase.invoke(any(), any())).thenReturn(Result.Success(true))

        val viewModel = BreedDetailViewModel(savedStateHandle, repository, toggleFavoriteUseCase)
        advanceUntilIdle()
        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
        assertTrue(state.breed.isFavorite)
        verify(toggleFavoriteUseCase).invoke("abys", false)
    }

    @Test
    fun `toggleFavorite does nothing when in Error state`() = runTest {
        whenever(repository.getBreedById("abys")).thenReturn(Result.Error(RuntimeException()))

        val viewModel = BreedDetailViewModel(savedStateHandle, repository, toggleFavoriteUseCase)
        advanceUntilIdle()
        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(toggleFavoriteUseCase, never()).invoke(any(), any())
    }

    @Test
    fun `toggleFavorite preserves state on error`() = runTest {
        whenever(repository.getBreedById("abys")).thenReturn(Result.Success(aBreed(isFavorite = false)))
        whenever(toggleFavoriteUseCase.invoke(any(), any())).thenReturn(Result.Error(RuntimeException()))

        val viewModel = BreedDetailViewModel(savedStateHandle, repository, toggleFavoriteUseCase)
        advanceUntilIdle()
        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
        assertFalse(state.breed.isFavorite)
    }
}
