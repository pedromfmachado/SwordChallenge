package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BreedListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository: BreedRepository = mock {
        onBlocking { getBreeds(any(), any()) } doReturn Result.Success(listOf(aBreed()))
        on { observeFavoriteIds() } doReturn flowOf(emptySet())
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
    fun `loads breeds on init`() = runTest {
        val breeds = listOf(aBreed(id = "a"), aBreed(id = "b"))
        whenever(repository.getBreeds(any(), any())).thenReturn(Result.Success(breeds))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(2, state.breeds.size)
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        whenever(repository.getBreeds(any(), any())).thenReturn(Result.Error(RuntimeException("Network error")))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Error
        assertEquals("Network error", state.message)
    }

    @Test
    fun `loadNextPage appends breeds`() = runTest {
        val page0 = (1..10).map { aBreed(id = "breed$it") }
        val page1 = (11..20).map { aBreed(id = "breed$it") }
        whenever(repository.getBreeds(eq(0), any())).thenReturn(Result.Success(page0))
        whenever(repository.getBreeds(eq(1), any())).thenReturn(Result.Success(page1))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(20, state.breeds.size)
        assertTrue(state.canLoadMore)
    }

    @Test
    fun `loadNextPage stops when partial page returned`() = runTest {
        val partialPage = (1..5).map { aBreed(id = "breed$it") }
        whenever(repository.getBreeds(any(), any())).thenReturn(Result.Success(partialPage))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `loadNextPage keeps current breeds on error`() = runTest {
        val page0 = (1..10).map { aBreed(id = "breed$it") }
        whenever(repository.getBreeds(eq(0), any())).thenReturn(Result.Success(page0))
        whenever(repository.getBreeds(eq(1), any())).thenReturn(Result.Error(RuntimeException()))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(10, state.breeds.size)
    }

    @Test
    fun `search filters breeds by name`() = runTest {
        val breeds = listOf(aBreed(id = "1", name = "Abyssinian"), aBreed(id = "2", name = "Bengal"))
        whenever(repository.getBreeds(any(), any())).thenReturn(Result.Success(breeds))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()
        viewModel.onSearchQueryChange("beng")
        testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(1, state.breeds.size)
        assertEquals("Bengal", state.breeds[0].name)
    }

    @Test
    fun `applies favorite status from observed ids`() = runTest {
        whenever(repository.getBreeds(any(), any())).thenReturn(Result.Success(listOf(aBreed(id = "abys"))))
        whenever(repository.observeFavoriteIds()).thenReturn(flowOf(setOf("abys")))

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertTrue(state.breeds[0].isFavorite)
    }

    @Test
    fun `toggleFavorite calls use case`() = runTest {
        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite(aBreed(id = "abc", isFavorite = true))
        advanceUntilIdle()

        verify(toggleFavoriteUseCase).invoke("abc", true)
    }
}
