package com.pedromfmachado.sword.catz.presentation.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pedromfmachado.sword.catz.R
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.DetailScreen

private const val TABS_ROUTE = "tabs"

sealed class BottomNavItem(
    @StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    data object List : BottomNavItem(
        titleResId = R.string.nav_list_label,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List,
        route = CatBreedsRoute.List.route
    )

    data object Favorites : BottomNavItem(
        titleResId = R.string.nav_favorites_label,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        route = CatBreedsRoute.Favorites.route
    )
}

@Composable
fun MainScreen() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = TABS_ROUTE
    ) {
        composable(TABS_ROUTE) {
            TabsScreen(
                onBreedClick = { breed ->
                    rootNavController.navigate(CatBreedsRoute.Detail.createRoute(breed.id))
                }
            )
        }

        composable(
            route = CatBreedsRoute.Detail.route,
            arguments = listOf(navArgument("breedId") { type = NavType.StringType })
        ) { backStackEntry ->
            val breedId = backStackEntry.arguments?.getString("breedId") ?: return@composable
            DetailScreen(
                breedId = breedId,
                onBackClick = { rootNavController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
