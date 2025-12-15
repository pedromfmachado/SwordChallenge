package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedDetailUiState
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedDetailViewModel
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common.ErrorContent
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common.LoadingContent

private val FavoriteActiveColor = Color(0xFFE91E63)
private val FavoriteInactiveColor = Color(0xFF757575)

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BreedDetailUiState.Loading -> LoadingContent(modifier = modifier)
        is BreedDetailUiState.Success -> {
            DetailScreenContent(
                breed = state.breed,
                onBackClick = onBackClick,
                onFavoriteClick = { viewModel.toggleFavorite() },
                modifier = modifier
            )
        }
        is BreedDetailUiState.Error -> ErrorContent(message = state.message, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreenContent(
    breed: Breed,
    onBackClick: () -> Unit,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteContentDescription = if (breed.isFavorite) {
        stringResource(R.string.a11y_breed_favorite_remove, breed.name)
    } else {
        stringResource(R.string.a11y_breed_favorite_add, breed.name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = breed.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.breed_detail_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onFavoriteClick(breed) },
                        modifier = Modifier.semantics {
                            contentDescription = favoriteContentDescription
                        }
                    ) {
                        Icon(
                            imageVector = if (breed.isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = null,
                            tint = if (breed.isFavorite) {
                                FavoriteActiveColor
                            } else {
                                FavoriteInactiveColor
                            }
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = breed.imageUrl,
                contentDescription = stringResource(
                    R.string.a11y_breed_image_description,
                    breed.name
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailSection(
                    label = stringResource(R.string.breed_origin_label),
                    value = breed.origin
                )

                DetailSection(
                    label = stringResource(R.string.breed_temperament_label),
                    value = breed.temperament
                )

                DetailSection(
                    label = stringResource(R.string.breed_lifespan_label),
                    value = stringResource(
                        R.string.breed_lifespan_years,
                        "${breed.lifespanLow} - ${breed.lifespanHigh}"
                    )
                )

                DetailSection(
                    label = stringResource(R.string.breed_description_label),
                    value = breed.description
                )
            }
        }
    }
}

@Composable
private fun DetailSection(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    DetailScreenContent(
        breed = PreviewData.persianBreed,
        onBackClick = {},
        onFavoriteClick = {}
    )
}
