package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BreedListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `breed list displays empty message when list is empty`() {
        composeTestRule.setContent {
            BreedList(
                breeds = emptyList(),
                onBreedClick = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule.onNodeWithText("No breeds available").assertIsDisplayed()
    }

    @Test
    fun `breed list displays all breed items when list has breeds`() {
        val breeds = PreviewData.breeds

        composeTestRule.setContent {
            BreedList(
                breeds = breeds,
                onBreedClick = {},
                onFavoriteClick = {}
            )
        }

        breeds.forEach { breed ->
            composeTestRule.onNodeWithText(breed.name).assertIsDisplayed()
        }
    }

    @Test
    fun `breed list does not display empty message when list has breeds`() {
        val breeds = PreviewData.breeds

        composeTestRule.setContent {
            BreedList(
                breeds = breeds,
                onBreedClick = {},
                onFavoriteClick = {}
            )
        }

        composeTestRule.onAllNodesWithText("No breeds available").assertCountEquals(0)
    }

    @Test
    fun `clicking breed item invokes onBreedClick callback`() {
        val breeds = PreviewData.breeds
        val onBreedClick = mock<(Breed) -> Unit>()

        composeTestRule.setContent {
            BreedList(
                breeds = breeds,
                onBreedClick = onBreedClick,
                onFavoriteClick = {}
            )
        }

        val firstBreed = breeds.first()
        composeTestRule.onNodeWithText(firstBreed.name).performClick()

        argumentCaptor<Breed>().apply {
            verify(onBreedClick).invoke(capture())
            assertEquals(firstBreed, firstValue)
        }
    }

    @Test
    fun `clicking favorite button invokes onFavoriteClick callback`() {
        val breeds = PreviewData.breeds
        val onFavoriteClick = mock<(Breed) -> Unit>()

        composeTestRule.setContent {
            BreedList(
                breeds = breeds,
                onBreedClick = {},
                onFavoriteClick = onFavoriteClick
            )
        }

        val firstBreed = breeds.first() // Persian - not favorite
        composeTestRule
            .onNodeWithContentDescription("Add ${firstBreed.name} to favorites")
            .performClick()

        argumentCaptor<Breed>().apply {
            verify(onFavoriteClick).invoke(capture())
            assertEquals(firstBreed, firstValue)
        }
    }
}
