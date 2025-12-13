package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    FavoritesScreenContent(
        favoriteBreeds = PreviewData.favoriteBreeds,
        averageLifespan = 12,
        onBreedClick = {}
    )
}
