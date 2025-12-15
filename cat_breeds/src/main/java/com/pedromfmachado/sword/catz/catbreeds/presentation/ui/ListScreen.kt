package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common.ErrorContent
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.common.LoadingContent
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListUiState
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListViewModel

@Composable
fun ListScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        ListScreenContent(
            uiState = uiState,
            onBreedClick = onBreedClick,
            onFavoriteClick = viewModel::toggleFavorite,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ListScreenContent(
    uiState: BreedListUiState,
    onBreedClick: (Breed) -> Unit,
    onFavoriteClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is BreedListUiState.Loading -> LoadingContent(modifier = modifier)
        is BreedListUiState.Success -> {
            BreedList(
                breeds = uiState.breeds,
                onBreedClick = onBreedClick,
                onFavoriteClick = onFavoriteClick,
                modifier = modifier,
            )
        }
        is BreedListUiState.Error -> ErrorContent(
            message = uiState.message,
            modifier = modifier,
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clearSearchDescription = stringResource(R.string.a11y_screen_list_clear_search)

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.screen_list_search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.semantics {
                        contentDescription = clearSearchDescription
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                    )
                }
            }
        },
        singleLine = true,
    )
}
