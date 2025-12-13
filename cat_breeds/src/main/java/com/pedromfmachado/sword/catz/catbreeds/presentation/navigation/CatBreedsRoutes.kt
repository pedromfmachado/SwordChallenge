package com.pedromfmachado.sword.catz.catbreeds.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class CatBreedsRoute(val route: String) {
    data object List : CatBreedsRoute("breeds_list")
    data object Favorites : CatBreedsRoute("breeds_favorites")
    data object Detail : CatBreedsRoute("breeds_detail/{$ARG_BREED_ID}") {
        val arguments = listOf(
            navArgument(ARG_BREED_ID) { type = NavType.StringType },
        )

        fun createRoute(breedId: String) = "breeds_detail/$breedId"
    }

    companion object {
        const val ARG_BREED_ID = "breedId"
    }
}
