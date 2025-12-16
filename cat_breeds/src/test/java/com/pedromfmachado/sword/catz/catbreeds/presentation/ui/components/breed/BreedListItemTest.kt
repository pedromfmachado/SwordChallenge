package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.test.aBreed
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BreedListItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `breed list item displays breed name`() {
        val breed = aBreed()

        composeTestRule.setContent {
            BreedListItem(
                breed = breed,
                onClick = {},
                onFavoriteClick = {},
            )
        }

        composeTestRule.onNodeWithText(breed.name).assertIsDisplayed()
    }

    @Test
    fun `breed list item shows remove from favorites description for favorite breed`() {
        val breed = aBreed(isFavorite = true)

        composeTestRule.setContent {
            BreedListItem(
                breed = breed,
                onClick = {},
                onFavoriteClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Remove ${breed.name} from favorites")
            .assertIsDisplayed()
    }

    @Test
    fun `breed list item shows add to favorites description for non-favorite breed`() {
        val breed = aBreed(isFavorite = false)

        composeTestRule.setContent {
            BreedListItem(
                breed = breed,
                onClick = {},
                onFavoriteClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add ${breed.name} to favorites")
            .assertIsDisplayed()
    }

    @Test
    fun `clicking card invokes onClick callback with correct breed`() {
        val breed = aBreed()
        val onClick = mock<(Breed) -> Unit>()

        composeTestRule.setContent {
            BreedListItem(
                breed = breed,
                onClick = onClick,
                onFavoriteClick = {},
            )
        }

        composeTestRule.onNodeWithText(breed.name).performClick()

        argumentCaptor<Breed>().apply {
            verify(onClick).invoke(capture())
            assertEquals(breed, firstValue)
        }
    }

    @Test
    fun `clicking favorite button invokes onFavoriteClick callback with correct breed`() {
        val breed = aBreed()
        val onFavoriteClick = mock<(Breed) -> Unit>()

        composeTestRule.setContent {
            BreedListItem(
                breed = breed,
                onClick = {},
                onFavoriteClick = onFavoriteClick,
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add ${breed.name} to favorites")
            .performClick()

        argumentCaptor<Breed>().apply {
            verify(onFavoriteClick).invoke(capture())
            assertEquals(breed, firstValue)
        }
    }
}
