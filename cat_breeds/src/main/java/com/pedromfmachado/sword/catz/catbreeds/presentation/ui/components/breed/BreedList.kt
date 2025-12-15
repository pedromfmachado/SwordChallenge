package com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common.LoadMoreIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedList(
    breeds: List<Breed>,
    onBreedClick: (Breed) -> Unit,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    isRefreshing: Boolean = false,
    isLoadingMore: Boolean = false,
    hasMorePages: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    onLoadMore: (() -> Unit)? = null
) {
    if (breeds.isEmpty() && !isRefreshing) {
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onRefresh?.invoke() },
            modifier = modifier.fillMaxSize()
        ) {
            val listState = rememberLazyListState()

            // Detect when user scrolls near the end
            val shouldLoadMore by remember {
                derivedStateOf {
                    val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val totalItems = listState.layoutInfo.totalItemsCount
                    lastVisibleItem >= totalItems - 3 && hasMorePages && !isLoadingMore
                }
            }

            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) {
                    onLoadMore?.invoke()
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding
            ) {
                items(
                    items = breeds,
                    key = { breed -> breed.id }
                ) { breed ->
                    BreedListItem(
                        breed = breed,
                        onClick = onBreedClick,
                        onFavoriteClick = onFavoriteClick
                    )
                }

                if (isLoadingMore) {
                    item {
                        LoadMoreIndicator()
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
        onFavoriteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
internal fun BreedListEmptyPreview() {
    BreedList(
        breeds = emptyList(),
        onBreedClick = {},
        onFavoriteClick = {}
    )
}
