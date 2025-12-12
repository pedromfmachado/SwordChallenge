package com.pedromfmachado.sword.presentation.ui.components.breed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedromfmachado.sword.R
import com.pedromfmachado.sword.data.mock.MockBreedData
import com.pedromfmachado.sword.domain.model.Breed

@Composable
fun BreedList(
    breeds: List<Breed>,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    if (breeds.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.breed_empty_message),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            items(
                items = breeds,
                key = { breed -> breed.id }
            ) { breed ->
                BreedListItem(
                    breed = breed,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BreedListPreview() {
    BreedList(
        breeds = MockBreedData.breeds.take(3),
        onFavoriteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun BreedListEmptyPreview() {
    BreedList(
        breeds = emptyList(),
        onFavoriteClick = {}
    )
}
