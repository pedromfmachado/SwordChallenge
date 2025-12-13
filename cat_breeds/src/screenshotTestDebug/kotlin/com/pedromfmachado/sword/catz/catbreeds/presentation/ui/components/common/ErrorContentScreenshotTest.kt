package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

class ErrorContentScreenshotTest {

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun ErrorContentPreviewTest() {
        ErrorContentPreview()
    }

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun ErrorContentDefaultPreviewTest() {
        ErrorContentDefaultPreview()
    }
}
