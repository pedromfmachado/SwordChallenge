package com.pedromfmachado.sword.catz.catbreeds.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BreedDto(
    val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    @param:Json(name = "life_span") val lifeSpan: String,
    val image: ImageDto?,
)

@JsonClass(generateAdapter = true)
data class ImageDto(
    val url: String,
)
