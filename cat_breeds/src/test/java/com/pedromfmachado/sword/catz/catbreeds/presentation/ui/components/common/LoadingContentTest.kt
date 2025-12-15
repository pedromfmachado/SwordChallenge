package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoadingContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `loading content displays CircularProgressIndicator`() {
        composeTestRule.setContent {
            LoadingContent()
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }
}
