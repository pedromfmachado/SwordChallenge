package com.pedromfmachado.sword.catz.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedromfmachado.sword.catz.catbreeds.presentation.navigation.CatBreedsRoute
import com.pedromfmachado.sword.catz.catbreeds.presentation.ui.DetailScreen

private const val TABS_ROUTE = "tabs"

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
            arguments = CatBreedsRoute.Detail.arguments
        ) {
            DetailScreen(
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
