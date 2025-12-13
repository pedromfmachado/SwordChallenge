package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.data.mock.MockBreedData
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed

private val FavoriteActiveColor = Color(0xFFE91E63)
private val FavoriteInactiveColor = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    breedId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val breed = MockBreedData.findBreedById(breedId)

    if (breed == null) {
        Text(text = "Breed not found")
        return
    }

    DetailScreenContent(
        breed = breed,
        onBackClick = onBackClick,
        onFavoriteClick = { /* No action for now */ },
        modifier = modifier
    )
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
                contentDescription = stringResource(R.string.a11y_breed_image_description, breed.name),
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
        breed = Breed(
            id = "1",
            name = "Persian",
            imageUrl = "https://cdn2.thecatapi.com/images/OGTWqNNOt.jpg",
            origin = "Iran (Persia)",
            temperament = "Affectionate, Loyal, Quiet, Gentle",
            description = "Persians are known for their long, luxurious coats and sweet, gentle personalities.",
            isFavorite = false
        ),
        onBackClick = {},
        onFavoriteClick = {}
    )
}
