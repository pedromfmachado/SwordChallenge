package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList
import com.pedromfmachado.sword.catz.catbreeds.presentation.viewmodel.BreedListViewModel
import com.pedromfmachado.sword.catz.catbreeds.preview.PreviewData

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

@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    BreedList(
        breeds = PreviewData.breeds,
        onBreedClick = {},
        onFavoriteClick = {}
    )
}
