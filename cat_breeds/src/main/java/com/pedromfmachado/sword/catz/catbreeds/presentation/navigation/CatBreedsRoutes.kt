package com.pedromfmachado.sword.catz.catbreeds.presentation.navigation

sealed class CatBreedsRoute(val route: String) {
    data object List : CatBreedsRoute("breeds_list")
    data object Favorites : CatBreedsRoute("breeds_favorites")
    data object Detail : CatBreedsRoute("breeds_detail/{breedId}") {
        fun createRoute(breedId: String) = "breeds_detail/$breedId"
    }
}
