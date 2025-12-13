package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.data.mock.MockBreedData
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList

@Composable
fun FavoritesScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteBreeds = MockBreedData.favoriteBreeds
    val averageLifespan = favoriteBreeds.takeIf { it.isNotEmpty() }
        ?.map { it.lifespanLow }
        ?.average()
        ?.toInt()

    Column(modifier = modifier.fillMaxSize()) {
        if (averageLifespan != null) {
            Text(
                text = stringResource(R.string.favorites_average_lifespan, averageLifespan),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        BreedList(
            breeds = favoriteBreeds,
            onBreedClick = onBreedClick,
            onFavoriteClick = { /* No action for now */ },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    FavoritesScreen(onBreedClick = {})
}
