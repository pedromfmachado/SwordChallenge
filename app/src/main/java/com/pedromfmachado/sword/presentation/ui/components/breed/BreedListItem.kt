package com.pedromfmachado.sword.presentation.ui.components.breed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pedromfmachado.sword.R
import com.pedromfmachado.sword.domain.model.Breed

@Composable
fun BreedListItem(
    breed: Breed,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteContentDescription = if (breed.isFavorite) {
        stringResource(R.string.a11y_breed_favorite_remove, breed.name)
    } else {
        stringResource(R.string.a11y_breed_favorite_add, breed.name)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = breed.imageUrl,
                contentDescription = stringResource(R.string.a11y_breed_image_description, breed.name),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = breed.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

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
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BreedListItemPreview() {
    BreedListItem(
        breed = Breed(
            id = "1",
            name = "Persian",
            imageUrl = "https://cdn2.thecatapi.com/images/OGTWqNNOt.jpg",
            isFavorite = false
        ),
        onFavoriteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BreedListItemFavoritePreview() {
    BreedListItem(
        breed = Breed(
            id = "1",
            name = "Persian",
            imageUrl = "https://cdn2.thecatapi.com/images/OGTWqNNOt.jpg",
            isFavorite = true
        ),
        onFavoriteClick = {}
    )
}
