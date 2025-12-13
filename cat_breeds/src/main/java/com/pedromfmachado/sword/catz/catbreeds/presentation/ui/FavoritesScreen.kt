package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedFavoritesUiState
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedFavoritesViewModel
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

@Composable
fun FavoritesScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedFavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BreedFavoritesUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BreedFavoritesUiState.Success -> {
            FavoritesScreenContent(
                favoriteBreeds = state.breeds,
                averageLifespan = state.averageLifespan,
                onBreedClick = onBreedClick,
                modifier = modifier
            )
        }
        is BreedFavoritesUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message ?: stringResource(R.string.screen_favorites_error_generic))
            }
        }
    }
}

@Composable
private fun FavoritesScreenContent(
    favoriteBreeds: List<Breed>,
    averageLifespan: Int?,
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier
) {
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
    FavoritesScreenContent(
        favoriteBreeds = PreviewData.favoriteBreeds,
        averageLifespan = 12,
        onBreedClick = {}
    )
}
