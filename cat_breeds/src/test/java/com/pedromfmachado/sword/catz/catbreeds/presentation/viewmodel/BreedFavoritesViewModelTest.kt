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
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class BreedFavoritesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private data class TestDependencies(
        val favoritesFlow: MutableSharedFlow<Result<List<Breed>>>,
        val toggleFavoriteUseCase: ToggleFavoriteUseCase,
        val viewModel: BreedFavoritesViewModel,
    )

    private fun createDependencies(): TestDependencies {
        val favoritesFlow = MutableSharedFlow<Result<List<Breed>>>(replay = 1)
        val repository: BreedRepository = mock {
            on { observeFavoriteBreeds() } doReturn favoritesFlow
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
        val viewModel = BreedFavoritesViewModel(repository, toggleFavoriteUseCase)
        return TestDependencies(favoritesFlow, toggleFavoriteUseCase, viewModel)
    }

    private suspend fun TestDependencies.emitAndAwaitSuccess(breeds: List<Breed>): BreedFavoritesUiState.Success {
        favoritesFlow.emit(Result.Success(breeds))
        return viewModel.uiState.first { it is BreedFavoritesUiState.Success } as BreedFavoritesUiState.Success
    }

    private suspend fun TestDependencies.emitAndAwaitError(exception: Exception): BreedFavoritesUiState.Error {
        favoritesFlow.emit(Result.Error(exception))
        return viewModel.uiState.first { it is BreedFavoritesUiState.Error } as BreedFavoritesUiState.Error
    }

    @Test
    fun `initial state is Loading before flow emits`() =
        runTest {
            val (_, _, viewModel) = createDependencies()

            assertEquals(BreedFavoritesUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `emits Success state when favorites flow emits`() =
        runTest {
            val favorites = listOf(BASE_BREED.copy(isFavorite = true))
            val deps = createDependencies()

            val state = deps.emitAndAwaitSuccess(favorites)

            assertEquals(favorites, state.breeds)
        }

    @Test
    fun `emits Error state when favorites flow emits error`() =
        runTest {
            val deps = createDependencies()

            val state = deps.emitAndAwaitError(RuntimeException("Database error"))

            assertEquals("Database error", state.message)
        }

    enum class AverageLifespanTestCase(
        val breeds: List<Breed>,
        val expectedAverage: Int?,
    ) {
        // (14+16)/2 = 15
        SingleBreed(
            breeds = listOf(BASE_BREED.copy(lifespanLow = 14, lifespanHigh = 16)),
            expectedAverage = 15,
        ),

        // Breed 1: avg 12, Breed 2: avg 15, Breed 3: avg 15 -> (12+15+15)/3 = 14
        MultipleBreeds(
            breeds = listOf(
                BASE_BREED.copy(id = "1", lifespanLow = 10, lifespanHigh = 14),
                BASE_BREED.copy(id = "2", lifespanLow = 14, lifespanHigh = 16),
                BASE_BREED.copy(id = "3", lifespanLow = 12, lifespanHigh = 18),
            ),
            expectedAverage = 14,
        ),
        EmptyList(
            breeds = emptyList(),
            expectedAverage = null,
        ),

        // Breed 1: avg 11, Breed 2: avg 15 -> (11+15)/2 = 13.0
        TruncatesToInt(
            breeds = listOf(
                BASE_BREED.copy(id = "1", lifespanLow = 10, lifespanHigh = 12),
                BASE_BREED.copy(id = "2", lifespanLow = 14, lifespanHigh = 16),
            ),
            expectedAverage = 13,
        ),

        // Breed 1: avg 10.5, Breed 2: avg 14.5 -> (10.5+14.5)/2 = 12.5 truncated to 12
        RoundsDownFractional(
            breeds = listOf(
                BASE_BREED.copy(id = "1", lifespanLow = 10, lifespanHigh = 11),
                BASE_BREED.copy(id = "2", lifespanLow = 14, lifespanHigh = 15),
            ),
            expectedAverage = 12,
        ),
    }

    @Test
    fun `calculates correct average lifespan`(
        @TestParameter testCase: AverageLifespanTestCase,
    ) = runTest {
        val deps = createDependencies()

        val state = deps.emitAndAwaitSuccess(testCase.breeds)

        assertEquals(testCase.expectedAverage, state.averageLifespan)
    }

    enum class ToggleFavoriteTestCase(
        val breed: Breed,
        val expectedIsFavorite: Boolean,
    ) {
        FavoriteBreed(BASE_BREED.copy(id = "abys", isFavorite = true), true),
        NonFavoriteBreed(BASE_BREED.copy(id = "abys", isFavorite = false), false),
    }

    @Test
    fun `toggleFavorite calls use case with correct isFavorite`(
        @TestParameter testCase: ToggleFavoriteTestCase,
    ) = runTest {
        val deps = createDependencies()
        deps.emitAndAwaitSuccess(listOf(testCase.breed))

        deps.viewModel.toggleFavorite(testCase.breed)

        verify(deps.toggleFavoriteUseCase).invoke(eq("abys"), eq(testCase.expectedIsFavorite))
    }

    @Test
    fun `Success state with empty list shows empty breeds`() =
        runTest {
            val deps = createDependencies()

            val state = deps.emitAndAwaitSuccess(emptyList())

            assertTrue(state.breeds.isEmpty())
        }

    @Test
    fun `Error state contains null message when exception has no message`() =
        runTest {
            val deps = createDependencies()

            val state = deps.emitAndAwaitError(RuntimeException())

            assertNull(state.message)
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
            isFavorite = true,
        )
    }
}
