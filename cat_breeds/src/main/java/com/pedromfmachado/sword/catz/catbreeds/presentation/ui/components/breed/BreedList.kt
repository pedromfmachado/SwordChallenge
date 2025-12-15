package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

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
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

@Composable
fun BreedList(
    breeds: List<Breed>,
    onBreedClick: (Breed) -> Unit,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (breeds.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.breed_empty_message),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            items(
                items = breeds,
                key = { breed -> breed.id },
            ) { breed ->
                BreedListItem(
                    breed = breed,
                    onClick = onBreedClick,
                    onFavoriteClick = onFavoriteClick,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun BreedListPreview() {
    BreedList(
        breeds = PreviewData.breeds,
        onBreedClick = {},
        onFavoriteClick = {},
    )
}

@Preview(showBackground = true)
@Composable
internal fun BreedListEmptyPreview() {
    BreedList(
        breeds = emptyList(),
        onBreedClick = {},
        onFavoriteClick = {},
    )
}
