package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class BreedDetailViewModelTest {
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
        val viewModel: BreedDetailViewModel,
    )

    private fun createDependencies(
        breedId: String = "abys",
        breedResult: Result<Breed> = Result.Success(BASE_BREED),
        toggleFavoriteResult: Result<Boolean>? = null,
    ): TestDependencies {
        val savedStateHandle = SavedStateHandle(mapOf(CatBreedsRoute.ARG_BREED_ID to breedId))
        val repository: BreedRepository = mock {
            onBlocking { getBreedById(breedId) } doReturn breedResult
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = if (toggleFavoriteResult != null) {
            mock {
                onBlocking { invoke(any(), any()) } doReturn toggleFavoriteResult
            }
        } else {
            mock()
        }
        val viewModel = BreedDetailViewModel(savedStateHandle, repository, toggleFavoriteUseCase)
        return TestDependencies(repository, toggleFavoriteUseCase, viewModel)
    }

    private fun assertSuccessState(viewModel: BreedDetailViewModel): BreedDetailUiState.Success {
        val state = viewModel.uiState.value
        assertTrue("Expected Success state but was $state", state is BreedDetailUiState.Success)
        return state as BreedDetailUiState.Success
    }

    private fun assertErrorState(viewModel: BreedDetailViewModel): BreedDetailUiState.Error {
        val state = viewModel.uiState.value
        assertTrue("Expected Error state but was $state", state is BreedDetailUiState.Error)
        return state as BreedDetailUiState.Error
    }

    @Test
    fun `initial state is Loading`() =
        runTest {
            val (_, _, viewModel) = createDependencies()

            assertEquals(BreedDetailUiState.Loading, viewModel.uiState.value)
        }

    @Test
    fun `loads breed on init and transitions to Success`() =
        runTest {
            val (_, _, viewModel) = createDependencies(breedResult = Result.Success(BASE_BREED))
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals(BASE_BREED, state.breed)
        }

    @Test
    fun `transitions to Error state when repository fails`() =
        runTest {
            val exception = RuntimeException("Breed not found")
            val (_, _, viewModel) = createDependencies(breedResult = Result.Error(exception))
            advanceUntilIdle()

            val state = assertErrorState(viewModel)
            assertEquals("Breed not found", state.message)
        }

    @Test
    fun `loads breed by id from savedStateHandle`() =
        runTest {
            val customBreed = BASE_BREED.copy(id = "beng", name = "Bengal")
            val (_, _, viewModel) = createDependencies(
                breedId = "beng",
                breedResult = Result.Success(customBreed),
            )
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertEquals("beng", state.breed.id)
            assertEquals("Bengal", state.breed.name)
        }

    enum class ToggleFavoriteTestCase(
        val initialIsFavorite: Boolean,
        val toggleResult: Boolean,
        val expectedIsFavorite: Boolean,
    ) {
        AddToFavorites(initialIsFavorite = false, toggleResult = true, expectedIsFavorite = true),
        RemoveFromFavorites(initialIsFavorite = true, toggleResult = false, expectedIsFavorite = false),
    }

    @Test
    fun `toggleFavorite updates breed isFavorite state`(
        @TestParameter testCase: ToggleFavoriteTestCase,
    ) = runTest {
        val breed = BASE_BREED.copy(isFavorite = testCase.initialIsFavorite)
        val (_, _, viewModel) = createDependencies(
            breedResult = Result.Success(breed),
            toggleFavoriteResult = Result.Success(testCase.toggleResult),
        )
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = assertSuccessState(viewModel)
        assertEquals(testCase.expectedIsFavorite, state.breed.isFavorite)
    }

    @Test
    fun `toggleFavorite calls use case with correct parameters`() =
        runTest {
            val breed = BASE_BREED.copy(id = "abys", isFavorite = false)
            val (_, toggleFavoriteUseCase, viewModel) = createDependencies(
                breedResult = Result.Success(breed),
                toggleFavoriteResult = Result.Success(true),
            )
            advanceUntilIdle()

            viewModel.toggleFavorite()
            advanceUntilIdle()

            verify(toggleFavoriteUseCase).invoke(eq("abys"), eq(false))
        }

    enum class ToggleBlockedTestCase(
        val breedResult: Result<Breed>,
        val shouldAdvance: Boolean,
    ) {
        ErrorState(Result.Error(RuntimeException("Error")), true),
        LoadingState(Result.Success(BASE_BREED), false),
    }

    @Test
    fun `toggleFavorite does nothing when not in Success state`(
        @TestParameter testCase: ToggleBlockedTestCase,
    ) = runTest {
        val (_, toggleFavoriteUseCase, viewModel) = createDependencies(breedResult = testCase.breedResult)
        if (testCase.shouldAdvance) {
            advanceUntilIdle()
        }

        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(toggleFavoriteUseCase, never()).invoke(any(), any())
    }

    @Test
    fun `toggleFavorite preserves state when use case returns error`() =
        runTest {
            val breed = BASE_BREED.copy(isFavorite = false)
            val (_, _, viewModel) = createDependencies(
                breedResult = Result.Success(breed),
                toggleFavoriteResult = Result.Error(RuntimeException("Database error")),
            )
            advanceUntilIdle()

            viewModel.toggleFavorite()
            advanceUntilIdle()

            val state = assertSuccessState(viewModel)
            assertFalse(state.breed.isFavorite)
        }

    @Test
    fun `Error state contains null message when exception has no message`() =
        runTest {
            val exception = RuntimeException()
            val (_, _, viewModel) = createDependencies(breedResult = Result.Error(exception))
            advanceUntilIdle()

            val state = assertErrorState(viewModel)
            assertEquals(null, state.message)
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
