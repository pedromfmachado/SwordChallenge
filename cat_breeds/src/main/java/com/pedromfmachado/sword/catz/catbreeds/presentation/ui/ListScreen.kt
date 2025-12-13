package com.pedromfmachado.sword.catz.catbreeds.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.catz.catbreeds.data.mock.MockBreedData
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.components.breed.BreedList

@Composable
fun ListScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier
) {
    BreedList(
        breeds = MockBreedData.breeds,
        onBreedClick = onBreedClick,
        onFavoriteClick = { /* No action for now */ },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    ListScreen(onBreedClick = {})
}
