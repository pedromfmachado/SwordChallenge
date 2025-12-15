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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pedromfmachado.sword.catz.R
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.FavoritesScreen
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.ListScreen

private sealed class BottomNavItem(
    @param:StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
) {
    data object List : BottomNavItem(
        titleResId = R.string.nav_list_label,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List,
        route = CatBreedsRoute.List.route,
    )

    data object Favorites : BottomNavItem(
        titleResId = R.string.nav_favorites_label,
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        route = CatBreedsRoute.Favorites.route,
    )
}

@Composable
fun TabsScreen(
    onBreedClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabsNavController = rememberNavController()
    val navItems = listOf(BottomNavItem.List, BottomNavItem.Favorites)

    val navBackStackEntry by tabsNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            tabsNavController.navigate(item.route) {
                                popUpTo(tabsNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = stringResource(item.titleResId),
                            )
                        },
                        label = { Text(text = stringResource(item.titleResId)) },
                    )
                }
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        NavHost(
            navController = tabsNavController,
            startDestination = CatBreedsRoute.List.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(CatBreedsRoute.List.route) {
                ListScreen(onBreedClick = onBreedClick)
            }
            composable(CatBreedsRoute.Favorites.route) {
                FavoritesScreen(onBreedClick = onBreedClick)
            }
        }
    }
}
