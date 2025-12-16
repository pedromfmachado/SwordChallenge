package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
import com.pedromfmachado.sword.catz.catbreeds.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class BreedListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

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
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(listOf(aBreed()))
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())

        assertEquals(BreedListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loads breeds on init and transitions to Success`() = runTest {
        val breeds = listOf(aBreed(id = "a"), aBreed(id = "b", name = "Bengal"))
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(breeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(2, state.breeds.size)
    }

    @Test
    fun `transitions to Error state when repository fails`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Error(RuntimeException("Network error"))
            on { observeFavoriteIds() } doReturn MutableSharedFlow()
        }

        val viewModel = BreedListViewModel(repository, mock())
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value as BreedListUiState.Error
        assertEquals("Network error", state.message)
    }

    @Test
    fun `loadNextPage increments page and fetches more breeds`() = runTest {
        val initialBreeds = (1..10).map { aBreed(id = "breed$it") }
        val nextPageBreeds = (11..20).map { aBreed(id = "breed$it") }
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(eq(0), any()) } doReturn Result.Success(initialBreeds)
            onBlocking { getBreeds(eq(1), any()) } doReturn Result.Success(nextPageBreeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(20, state.breeds.size)
    }

    @Test
    fun `loadNextPage does nothing when no more pages`() = runTest {
        val breeds = (1..5).map { aBreed(id = "breed$it") }
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(breeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(5, state.breeds.size)
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `loadNextPage does nothing when already loading`() = runTest {
        val initialBreeds = (1..10).map { aBreed(id = "breed$it") }
        val nextPageBreeds = (11..15).map { aBreed(id = "breed$it") }
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(eq(0), any()) } doReturn Result.Success(initialBreeds)
            onBlocking { getBreeds(eq(1), any()) } doReturn Result.Success(nextPageBreeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.loadNextPage()
        testDispatcher.scheduler.runCurrent()
        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(15, state.breeds.size)
    }

    @Test
    fun `loadNextPage reverts page on error`() = runTest {
        val initialBreeds = (1..10).map { aBreed(id = "breed$it") }
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(eq(0), any()) } doReturn Result.Success(initialBreeds)
            onBlocking { getBreeds(eq(1), any()) } doReturn Result.Error(RuntimeException("Error"))
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(10, state.breeds.size)
    }

    @Test
    fun `search filters breeds by name case insensitive`() = runTest {
        val breeds = listOf(
            aBreed(id = "1", name = "Abyssinian"),
            aBreed(id = "2", name = "Bengal"),
            aBreed(id = "3", name = "Persian"),
        )
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(breeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.onSearchQueryChange("eng")
        testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(1, state.breeds.size)
        assertEquals("Bengal", state.breeds[0].name)
    }

    @Test
    fun `search with empty query shows all breeds`() = runTest {
        val breeds = listOf(
            aBreed(id = "1", name = "Abyssinian"),
            aBreed(id = "2", name = "Bengal"),
        )
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(breeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.onSearchQueryChange("abys")
        testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("")
        testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(2, state.breeds.size)
    }

    @Test
    fun `searchQuery state is updated immediately`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(listOf(aBreed()))
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        viewModel.onSearchQueryChange("test")

        assertEquals("test", viewModel.searchQuery.value)
    }

    @Test
    fun `favorite ids are observed and applied to breeds`() = runTest {
        val breeds = listOf(aBreed(id = "abys"))
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(breeds)
            on { observeFavoriteIds() } doReturn flowOf(setOf("abys"))
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertTrue(state.breeds[0].isFavorite)
    }

    @Test
    fun `toggleFavorite calls use case with correct isFavorite`(
        @TestParameter isFavorite: Boolean,
    ) = runTest {
        val breed = aBreed(id = "abc", isFavorite = isFavorite)
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(listOf(breed))
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()

        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite(breed)
        advanceUntilIdle()

        verify(toggleFavoriteUseCase).invoke("abc", isFavorite)
    }

    enum class CanLoadMoreTestCase(
        val pageSize: Int,
        val expectedCanLoadMore: Boolean,
    ) {
        FullPage(10, true),
        PartialPage(5, false),
    }

    @Test
    fun `Success state has correct canLoadMore based on page size`(
        @TestParameter testCase: CanLoadMoreTestCase,
    ) = runTest {
        val breeds = (1..testCase.pageSize).map { aBreed(id = "breed$it") }
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn Result.Success(breeds)
            on { observeFavoriteIds() } doReturn flowOf(emptySet())
        }

        val viewModel = BreedListViewModel(repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedListUiState.Success
        assertEquals(testCase.expectedCanLoadMore, state.canLoadMore)
    }
}
