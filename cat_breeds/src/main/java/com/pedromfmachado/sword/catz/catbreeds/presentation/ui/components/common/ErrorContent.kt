package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.R

@Composable
fun ErrorContent(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message ?: stringResource(R.string.error_generic))
    }
}

@Preview(showBackground = true)
@Composable
internal fun ErrorContentPreview() {
    ErrorContent(message = "Something weird happened")
}

@Preview(showBackground = true)
@Composable
internal fun ErrorContentDefaultPreview() {
    ErrorContent()
}
