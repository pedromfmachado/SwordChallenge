package com.pedromfmachado.sword.catz.catbreeds.data.test

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.ImageDto

fun aBreedDto(
    id: String = "abys",
    name: String = "Abyssinian",
    origin: String = "Egypt",
    temperament: String = "Active, Energetic",
    description: String = "The Abyssinian is easy to care for",
    lifeSpan: String = "14 - 15",
    image: ImageDto? = ImageDto("https://example.com/cat.jpg"),
) = BreedDto(
    id = id,
    name = name,
    origin = origin,
    temperament = temperament,
    description = description,
    lifeSpan = lifeSpan,
    image = image,
)
