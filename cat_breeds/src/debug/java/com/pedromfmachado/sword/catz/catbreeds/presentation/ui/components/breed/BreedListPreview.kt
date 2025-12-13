package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

@Preview(showBackground = true)
@Composable
private fun BreedListPreview() {
    BreedList(
        breeds = PreviewData.breeds,
        onBreedClick = {},
        onFavoriteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BreedListEmptyPreview() {
    BreedList(
        breeds = emptyList(),
        onBreedClick = {},
        onFavoriteClick = {}
    )
}
