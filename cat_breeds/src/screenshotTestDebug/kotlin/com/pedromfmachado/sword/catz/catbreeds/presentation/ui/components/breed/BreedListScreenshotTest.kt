package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

class BreedListScreenshotTest {

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun BreedListPreviewTest() {
        BreedListPreview()
    }

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun BreedListEmptyPreviewTest() {
        BreedListEmptyPreview()
    }
}
