package com.pedromfmachado.sword.catz.catbreeds.test

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed

fun aBreed(
    id: String = "1",
    name: String = "Test Breed",
    imageUrl: String = "https://example.com/cat.jpg",
    origin: String = "Test Origin",
    temperament: String = "Friendly, Playful",
    description: String = "A test breed description.",
    lifespanLow: Int = 12,
    lifespanHigh: Int = 16,
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
