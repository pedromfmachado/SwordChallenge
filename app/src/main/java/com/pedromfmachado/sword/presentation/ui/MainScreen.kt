package com.pedromfmachado.sword.presentation.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pedromfmachado.sword.R
import com.pedromfmachado.sword.data.mock.MockBreedData
import com.pedromfmachado.sword.presentation.ui.components.breed.BreedList

sealed class BottomNavItem(
    @StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object List : BottomNavItem(
        titleResId = R.string.nav_list_label,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List
    )

    data object Favorites : BottomNavItem(
        titleResId = R.string.nav_favorites_label,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )
}

@Composable
fun MainScreen() {
    val navItems = listOf(BottomNavItem.List, BottomNavItem.Favorites)
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedIndex == index) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = stringResource(item.titleResId)
                            )
                        },
                        label = { Text(text = stringResource(item.titleResId)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedIndex) {
            0 -> ListScreen(modifier = Modifier.padding(innerPadding))
            1 -> FavoritesScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ListScreen(modifier: Modifier = Modifier) {
    BreedList(
        breeds = MockBreedData.breeds,
        onBreedClick = { /* No action for now */ },
        onFavoriteClick = { /* No action for now */ },
        modifier = modifier
    )
}

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    BreedList(
        breeds = MockBreedData.favoriteBreeds,
        onBreedClick = { /* No action for now */ },
        onFavoriteClick = { /* No action for now */ },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen()
}