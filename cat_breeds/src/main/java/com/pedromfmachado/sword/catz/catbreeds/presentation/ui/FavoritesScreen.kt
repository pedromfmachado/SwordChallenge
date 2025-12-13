package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.data.mock.MockBreedData
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    BreedList(
        breeds = MockBreedData.favoriteBreeds,
        onBreedClick = { /* No action for now */ },
        onFavoriteClick = { /* No action for now */ },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    FavoritesScreen()
}
