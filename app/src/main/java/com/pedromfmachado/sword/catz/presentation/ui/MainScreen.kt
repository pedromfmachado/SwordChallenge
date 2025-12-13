package com.pedromfmachado.sword.catz.presentation.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pedromfmachado.sword.catz.R
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.DetailScreen
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.FavoritesScreen
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.ListScreen

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
    val navController = rememberNavController()
    val navItems = listOf(BottomNavItem.List, BottomNavItem.Favorites)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = navItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CatBreedsRoute.List.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CatBreedsRoute.List.route) {
                ListScreen(
                    onBreedClick = { breed ->
                        navController.navigate(CatBreedsRoute.Detail.createRoute(breed.id))
                    }
                )
            }
            composable(CatBreedsRoute.Favorites.route) {
                FavoritesScreen(
                    onBreedClick = { breed ->
                        navController.navigate(CatBreedsRoute.Detail.createRoute(breed.id))
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
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
