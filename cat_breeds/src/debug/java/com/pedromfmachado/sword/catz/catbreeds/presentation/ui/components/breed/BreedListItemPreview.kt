package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

@Preview(showBackground = true)
@Composable
private fun BreedListItemPreview() {
    BreedListItem(
        breed = PreviewData.persianBreed,
        onClick = {},
        onFavoriteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BreedListItemFavoritePreview() {
    BreedListItem(
        breed = PreviewData.maineCoonBreed,
        onClick = {},
        onFavoriteClick = {}
    )
}
