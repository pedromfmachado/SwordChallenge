package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

private const val LOAD_MORE_THRESHOLD = 3

@Composable
fun BreedList(
    breeds: List<Breed>,
    onBreedClick: (Breed) -> Unit,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    isLoadingMore: Boolean = false,
    canLoadMore: Boolean = false,
    onLoadMore: () -> Unit = {},
) {
    if (breeds.isEmpty() && !isLoadingMore) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.cat_empty),
                    contentDescription = null,
                    modifier = Modifier.size(180.dp),
                )
                Text(
                    text = stringResource(R.string.breed_empty_message),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    } else {
        val listState = rememberLazyListState()

        // Detect when user scrolls near the end
        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                canLoadMore && lastVisibleIndex >= totalItems - LOAD_MORE_THRESHOLD
            }
        }

        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) {
                onLoadMore()
            }
        }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = listState,
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

            if (isLoadingMore) {
                item(key = "loading_indicator") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
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

@Preview(showBackground = true)
@Composable
internal fun BreedListLoadingMorePreview() {
    BreedList(
        breeds = PreviewData.breeds,
        onBreedClick = {},
        onFavoriteClick = {},
        isLoadingMore = true,
    )
}
