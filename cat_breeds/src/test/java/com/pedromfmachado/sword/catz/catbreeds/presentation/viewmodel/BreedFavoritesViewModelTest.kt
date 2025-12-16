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
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class BreedFavoritesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val favoritesFlow: MutableSharedFlow<Result<List<Breed>>> =
        MutableSharedFlow<Result<List<Breed>>>(replay = 1)
    private val repository: BreedRepository = mock {
        on { observeFavoriteBreeds() } doReturn favoritesFlow
    }
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
    private val viewModel: BreedFavoritesViewModel = BreedFavoritesViewModel(repository, toggleFavoriteUseCase)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading before flow emits`() = runTest {
        assertEquals(BreedFavoritesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `emits Success state when favorites flow emits`() = runTest {
        val favorites = listOf(aBreed(isFavorite = true))

        val state = emitAndAwaitSuccess(favorites)

        assertEquals(favorites, state.breeds)
    }

    @Test
    fun `emits Error state when favorites flow emits error`() = runTest {

        val state = emitAndAwaitError(RuntimeException("Database error"))

        assertEquals("Database error", state.message)
    }

    @Test
    fun `calculates correct average lifespan`(
        @TestParameter testCase: AverageLifespanTestCase,
    ) = runTest {
        val state = emitAndAwaitSuccess(testCase.breeds)

        assertEquals(testCase.expectedAverage, state.averageLifespan)
    }

    @Test
    fun `toggleFavorite calls use case with correct isFavorite`(
        @TestParameter isFavorite: Boolean,
    ) = runTest {
        val breed = aBreed(id = "abc", isFavorite = isFavorite)

        viewModel.toggleFavorite(breed)

        verify(toggleFavoriteUseCase).invoke("abc", isFavorite)
    }

    @Test
    fun `Success state with empty list shows empty breeds`() = runTest {
        val state = emitAndAwaitSuccess(emptyList())

        assertTrue(state.breeds.isEmpty())
    }

    @Test
    fun `Error state contains null message when exception has no message`() = runTest {
        val state = emitAndAwaitError(RuntimeException())

        assertNull(state.message)
    }

    private suspend fun emitAndAwaitSuccess(breeds: List<Breed>): BreedFavoritesUiState.Success {
        favoritesFlow.emit(Result.Success(breeds))
        return viewModel.uiState.first { it is BreedFavoritesUiState.Success } as BreedFavoritesUiState.Success
    }

    private suspend fun emitAndAwaitError(exception: Exception): BreedFavoritesUiState.Error {
        favoritesFlow.emit(Result.Error(exception))
        return viewModel.uiState.first { it is BreedFavoritesUiState.Error } as BreedFavoritesUiState.Error
    }

    enum class AverageLifespanTestCase(
        val breeds: List<Breed>,
        val expectedAverage: Int?,
    ) {
        SingleBreed(
            breeds = listOf(aBreed(lifespanLow = 14, lifespanHigh = 16)),
            expectedAverage = 15,
        ),
        MultipleBreeds(
            breeds = listOf(
                aBreed(lifespanLow = 10, lifespanHigh = 14),
                aBreed(lifespanLow = 14, lifespanHigh = 16),
                aBreed(lifespanLow = 12, lifespanHigh = 18),
            ),
            expectedAverage = 14,
        ),
        EmptyList(
            breeds = emptyList(),
            expectedAverage = null,
        ),
        TruncatesToInt(
            breeds = listOf(
                aBreed(lifespanLow = 10, lifespanHigh = 12),
                aBreed(lifespanLow = 14, lifespanHigh = 16),
            ),
            expectedAverage = 13,
        ),
        RoundsDownFractional(
            breeds = listOf(
                aBreed(lifespanLow = 10, lifespanHigh = 11),
                aBreed(lifespanLow = 14, lifespanHigh = 15),
            ),
            expectedAverage = 12,
        ),
    }
}
