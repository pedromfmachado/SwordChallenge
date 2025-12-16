package com.pedromfmachado.sword.catz.catbreeds.domain.test

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed

fun aBreed(
    id: String = "abys",
    name: String = "Abyssinian",
    imageUrl: String = "https://example.com/cat.jpg",
    origin: String = "Egypt",
    temperament: String = "Active, Energetic",
    description: String = "The Abyssinian is easy to care for",
    lifespanLow: Int = 14,
    lifespanHigh: Int = 15,
    isFavorite: Boolean = false,
) = Breed(
    id = id,
    name = name,
    imageUrl = imageUrl,
    origin = origin,
    temperament = temperament,
    description = description,
    lifespanLow = lifespanLow,
    lifespanHigh = lifespanHigh,
    isFavorite = isFavorite,
)
