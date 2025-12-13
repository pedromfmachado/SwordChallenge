package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListViewModel

@Composable
fun ListScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreedListViewModel = hiltViewModel()
) {
    BreedList(
        breeds = viewModel.breeds,
        onBreedClick = onBreedClick,
        onFavoriteClick = { /* No action for now */ },
        modifier = modifier
    )
}
