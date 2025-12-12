package com.pedromfmachado.sword.data.mock

import com.pedromfmachado.sword.domain.model.Breed

object MockBreedData {
    val breeds = listOf(
        Breed(
            id = "1",
            name = "Persian",
            imageUrl = "https://cdn2.thecatapi.com/images/OGTWqNNOt.jpg",
            isFavorite = false
        ),
        Breed(
            id = "2",
            name = "Maine Coon",
            imageUrl = "https://cdn2.thecatapi.com/images/OOD3VXAQn.jpg",
            isFavorite = true
        ),
        Breed(
            id = "3",
            name = "Siamese",
            imageUrl = "https://cdn2.thecatapi.com/images/ai6Jps4sx.jpg",
            isFavorite = false
        ),
        Breed(
            id = "4",
            name = "British Shorthair",
            imageUrl = "https://cdn2.thecatapi.com/images/s4wQfYoEk.jpg",
            isFavorite = true
        ),
        Breed(
            id = "5",
            name = "Ragdoll",
            imageUrl = "https://cdn2.thecatapi.com/images/oGefY4YoG.jpg",
            isFavorite = false
        ),
        Breed(
            id = "6",
            name = "Bengal",
            imageUrl = "https://cdn2.thecatapi.com/images/O3btzLlsO.png",
            isFavorite = true
        ),
        Breed(
            id = "7",
            name = "Abyssinian",
            imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            isFavorite = false
        ),
        Breed(
            id = "8",
            name = "Scottish Fold",
            imageUrl = "https://cdn2.thecatapi.com/images/o9t0LDcsa.jpg",
            isFavorite = false
        )
    )

    val favoriteBreeds: List<Breed>
        get() = breeds.filter { it.isFavorite }
}
