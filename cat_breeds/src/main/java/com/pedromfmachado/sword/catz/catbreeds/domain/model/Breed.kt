package com.pedromfmachado.sword.catz.catbreeds.domain.model

data class Breed(
    val id: String,
    val name: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
)
