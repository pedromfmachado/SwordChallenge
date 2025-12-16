package com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
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
import org.junit.Assert.assertNull
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

    private fun savedStateHandle(breedId: String = "abys") =
        SavedStateHandle(mapOf(CatBreedsRoute.ARG_BREED_ID to breedId))

    @Test
    fun `initial state is Loading`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Success(aBreed())
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, mock())

        assertEquals(BreedDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loads breed on init and transitions to Success`() = runTest {
        val breed = aBreed()
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Success(breed)
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
        assertEquals(breed, state.breed)
    }

    @Test
    fun `transitions to Error state when repository fails`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Error(RuntimeException("Breed not found"))
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Error
        assertEquals("Breed not found", state.message)
    }

    @Test
    fun `loads breed by id from savedStateHandle`() = runTest {
        val customBreed = aBreed(id = "beng", name = "Bengal")
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("beng") } doReturn Result.Success(customBreed)
        }

        val viewModel = BreedDetailViewModel(savedStateHandle("beng"), repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
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
        val breed = aBreed(isFavorite = testCase.initialIsFavorite)
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Success(breed)
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock {
            onBlocking { invoke(any(), any()) } doReturn Result.Success(testCase.toggleResult)
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
        assertEquals(testCase.expectedIsFavorite, state.breed.isFavorite)
    }

    @Test
    fun `toggleFavorite calls use case with correct parameters`() = runTest {
        val breed = aBreed(id = "abys", isFavorite = false)
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Success(breed)
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock {
            onBlocking { invoke(any(), any()) } doReturn Result.Success(true)
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(toggleFavoriteUseCase).invoke(eq("abys"), eq(false))
    }

    @Test
    fun `toggleFavorite does nothing when in Error state`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Error(RuntimeException("Error"))
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(toggleFavoriteUseCase, never()).invoke(any(), any())
    }

    @Test
    fun `toggleFavorite does nothing when in Loading state`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Success(aBreed())
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, toggleFavoriteUseCase)
        // Don't advance - stay in Loading state

        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(toggleFavoriteUseCase, never()).invoke(any(), any())
    }

    @Test
    fun `toggleFavorite preserves state when use case returns error`() = runTest {
        val breed = aBreed(isFavorite = false)
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Success(breed)
        }
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock {
            onBlocking { invoke(any(), any()) } doReturn Result.Error(RuntimeException("Database error"))
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, toggleFavoriteUseCase)
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Success
        assertFalse(state.breed.isFavorite)
    }

    @Test
    fun `Error state contains null message when exception has no message`() = runTest {
        val repository: BreedRepository = mock {
            onBlocking { getBreedById("abys") } doReturn Result.Error(RuntimeException())
        }

        val viewModel = BreedDetailViewModel(savedStateHandle(), repository, mock())
        advanceUntilIdle()

        val state = viewModel.uiState.value as BreedDetailUiState.Error
        assertNull(state.message)
    }
}
