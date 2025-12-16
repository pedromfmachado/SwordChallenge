package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
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

    private data class TestDependencies(
        val repository: BreedRepository,
        val toggleFavoriteUseCase: ToggleFavoriteUseCase,
        val viewModel: BreedListViewModel,
    )

    private fun createDependencies(
        breedsResult: Result<List<Breed>> = Result.Success(listOf(BASE_BREED)),
        favoriteIds: Set<String> = emptySet(),
    ): TestDependencies {
        val repository: BreedRepository = mock {
            onBlocking { getBreeds(any(), any()) } doReturn breedsResult
            on { observeFavoriteIds() } doReturn flowOf(favoriteIds)
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
        val viewModel = BreedListViewModel(repository, toggleFavoriteUseCase)
        return TestDependencies(repository, toggleFavoriteUseCase, viewModel)
    }

    private fun createBreedsPage(
        startIndex: Int,
        count: Int,
    ): List<Breed> = (startIndex until startIndex + count).map { BASE_BREED.copy(id = "breed$it") }

    private fun assertSuccessState(viewModel: BreedListViewModel): BreedListUiState.Success {
        val state = viewModel.uiState.value
        assertTrue("Expected Success state but was $state", state is BreedListUiState.Success)
        return state as BreedListUiState.Success
    }

    private fun assertErrorState(viewModel: BreedListViewModel): BreedListUiState.Error {
        val state = viewModel.uiState.value
        assertTrue("Expected Error state but was $state", state is BreedListUiState.Error)
        return state as BreedListUiState.Error
    }

    @Test
    fun `initial state is Loading`() =
        runTest {
            val (_, _, viewModel) = createDependencies()

            assertEquals(BreedListUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `loads breeds on init and transitions to Success`() =
        runTest {
            val breeds = listOf(BASE_BREED, BASE_BREED.copy(id = "b", name = "Bengal"))
            val (_, _, viewModel) = createDependencies(breedsResult = Result.Success(breeds))
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(2, state.breeds.size)
        }

    @Test
    fun `transitions to Error state when repository fails`() =
        runTest {
            val exception = RuntimeException("Network error")
            val favoriteIdsFlow = MutableSharedFlow<Set<String>>()
            val repository: BreedRepository = mock {
                onBlocking { getBreeds(any(), any()) } doReturn Result.Error(exception)
                on { observeFavoriteIds() } doReturn favoriteIdsFlow
            }
            val viewModel = BreedListViewModel(repository, mock())
            testDispatcher.scheduler.runCurrent()

            val state = assertErrorState(viewModel)
            assertEquals("Network error", state.message)
        }

    @Test
    fun `loadNextPage increments page and fetches more breeds`() =
        runTest {
            val initialBreeds = createBreedsPage(1, 10)
            val nextPageBreeds = createBreedsPage(11, 10)

            val repository: BreedRepository = mock {
                onBlocking { getBreeds(eq(0), any()) } doReturn Result.Success(initialBreeds)
                onBlocking { getBreeds(eq(1), any()) } doReturn Result.Success(nextPageBreeds)
                on { observeFavoriteIds() } doReturn flowOf(emptySet())
            }
            val viewModel = BreedListViewModel(repository, mock())
            advanceUntilIdle()

            viewModel.loadNextPage()
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(20, state.breeds.size)
        }

    @Test
    fun `loadNextPage does nothing when no more pages`() =
        runTest {
            val breeds = createBreedsPage(1, 5)
            val (_, _, viewModel) = createDependencies(breedsResult = Result.Success(breeds))
            advanceUntilIdle()

            viewModel.loadNextPage()
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(5, state.breeds.size)
            assertFalse(state.canLoadMore)
        }

    @Test
    fun `loadNextPage does nothing when already loading`() =
        runTest {
            val initialBreeds = createBreedsPage(1, 10)
            val nextPageBreeds = createBreedsPage(11, 5)
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

            val state = assertSuccessState(viewModel)
            assertEquals(15, state.breeds.size)
        }

    @Test
    fun `loadNextPage reverts page on error`() =
        runTest {
            val initialBreeds = createBreedsPage(1, 10)

            val repository: BreedRepository = mock {
                onBlocking { getBreeds(eq(0), any()) } doReturn Result.Success(initialBreeds)
                onBlocking { getBreeds(eq(1), any()) } doReturn Result.Error(RuntimeException("Error"))
                on { observeFavoriteIds() } doReturn flowOf(emptySet())
            }
            val viewModel = BreedListViewModel(repository, mock())
            advanceUntilIdle()

            viewModel.loadNextPage()
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(10, state.breeds.size)
        }

    @Test
    fun `search filters breeds by name case insensitive`() =
        runTest {
            val breeds = listOf(
                BASE_BREED.copy(id = "1", name = "Abyssinian"),
                BASE_BREED.copy(id = "2", name = "Bengal"),
                BASE_BREED.copy(id = "3", name = "Persian"),
            )
            val (_, _, viewModel) = createDependencies(breedsResult = Result.Success(breeds))
            advanceUntilIdle()

            viewModel.onSearchQueryChange("eng")
            testDispatcher.scheduler.advanceTimeBy(350)
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(1, state.breeds.size)
            assertEquals("Bengal", state.breeds[0].name)
        }

    @Test
    fun `search with empty query shows all breeds`() =
        runTest {
            val breeds = listOf(
                BASE_BREED.copy(id = "1", name = "Abyssinian"),
                BASE_BREED.copy(id = "2", name = "Bengal"),
            )
            val (_, _, viewModel) = createDependencies(breedsResult = Result.Success(breeds))
            advanceUntilIdle()

            viewModel.onSearchQueryChange("abys")
            testDispatcher.scheduler.advanceTimeBy(350)
            advanceUntilIdle()

            viewModel.onSearchQueryChange("")
            testDispatcher.scheduler.advanceTimeBy(350)
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(2, state.breeds.size)
        }

    @Test
    fun `searchQuery state is updated immediately`() =
        runTest {
            val (_, _, viewModel) = createDependencies()
            advanceUntilIdle()

            viewModel.onSearchQueryChange("test")

            assertEquals("test", viewModel.searchQuery.value)
        }

    @Test
    fun `favorite ids are observed and applied to breeds`() =
        runTest {
            val breeds = listOf(BASE_BREED.copy(id = "abys"))
            val (_, _, viewModel) = createDependencies(
                breedsResult = Result.Success(breeds),
                favoriteIds = setOf("abys"),
            )
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertTrue(state.breeds[0].isFavorite)
        }

    enum class ToggleFavoriteTestCase(
        val isFavorite: Boolean,
    ) {
        NonFavorite(false),
        Favorite(true),
    }

    @Test
    fun `toggleFavorite calls use case with correct isFavorite`(
        @TestParameter testCase: ToggleFavoriteTestCase,
    ) = runTest {
        val breed = BASE_BREED.copy(isFavorite = testCase.isFavorite)
        val (_, toggleFavoriteUseCase, viewModel) = createDependencies(
            breedsResult = Result.Success(listOf(breed)),
        )
        advanceUntilIdle()

        viewModel.toggleFavorite(breed)
        advanceUntilIdle()

        verify(toggleFavoriteUseCase).invoke(breed.id, testCase.isFavorite)
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
        val breeds = createBreedsPage(1, testCase.pageSize)
        val (_, _, viewModel) = createDependencies(breedsResult = Result.Success(breeds))
        advanceUntilIdle()

        val state = assertSuccessState(viewModel)
        assertEquals(testCase.expectedCanLoadMore, state.canLoadMore)
    }

    companion object {
        private val BASE_BREED = Breed(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "The Abyssinian is easy to care for",
            lifespanLow = 14,
            lifespanHigh = 15,
            imageUrl = "https://example.com/cat.jpg",
            isFavorite = false,
        )
    }
}
