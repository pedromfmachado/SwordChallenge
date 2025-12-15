package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

class BreedListItemScreenshotTest {
    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun BreedListItemPreviewTest() {
        BreedListItemPreview()
    }

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun BreedListItemFavoritePreviewTest() {
        BreedListItemFavoritePreview()
    }
}
