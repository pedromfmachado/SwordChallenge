package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.pedromfmachado.sword.catz.catbreeds.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListUiState
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListViewModel

@Composable
fun ListScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is BreedListUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is BreedListUiState.Success -> {
            BreedList(
                breeds = state.breeds,
                onBreedClick = onBreedClick,
                onFavoriteClick = { /* No action for now */ },
                modifier = modifier
            )
        }
        is BreedListUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message ?: stringResource(R.string.screen_list_error_generic))
            }
        }
    }
}
