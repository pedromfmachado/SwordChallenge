package com.pedromfmachado.sword.catz.catbreeds.domain.model

data class Breed(
    val id: String,
    val name: String,
    val imageUrl: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val lifespanLow: Int,
    val lifespanHigh: Int,
    val isFavorite: Boolean = false
)
