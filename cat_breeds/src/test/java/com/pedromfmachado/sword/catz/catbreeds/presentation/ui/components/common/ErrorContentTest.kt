package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ErrorContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `error content displays custom message`() {
        val customMessage = "Network error occurred"

        composeTestRule.setContent {
            ErrorContent(message = customMessage)
        }

        composeTestRule.onNodeWithText(customMessage).assertIsDisplayed()
    }

    @Test
    fun `error content displays generic message when message is null`() {
        composeTestRule.setContent {
            ErrorContent(message = null)
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }
}
