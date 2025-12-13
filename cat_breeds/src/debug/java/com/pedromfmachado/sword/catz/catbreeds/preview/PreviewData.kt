package com.pedromfmachado.sword.catz.catbreeds.preview

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed

object PreviewData {

    val persianBreed = Breed(
        id = "1",
        name = "Persian",
        imageUrl = "https://cdn2.thecatapi.com/images/OGTWqNNOt.jpg",
        origin = "Iran (Persia)",
        temperament = "Affectionate, Loyal, Quiet, Gentle",
        description = "Persians are known for their long, luxurious coats and sweet, gentle personalities.",
        lifespanLow = 12,
        lifespanHigh = 17,
        isFavorite = false
    )

    val maineCoonBreed = Breed(
        id = "2",
        name = "Maine Coon",
        imageUrl = "https://cdn2.thecatapi.com/images/OOD3VXAQn.jpg",
        origin = "United States",
        temperament = "Adaptable, Intelligent, Playful, Gentle",
        description = "Maine Coons are one of the largest domestic cat breeds, known for their friendly, sociable nature.",
        lifespanLow = 12,
        lifespanHigh = 15,
        isFavorite = true
    )

    val siameseBreed = Breed(
        id = "3",
        name = "Siamese",
        imageUrl = "https://cdn2.thecatapi.com/images/ai6Jps4sx.jpg",
        origin = "Thailand",
        temperament = "Active, Vocal, Social, Intelligent",
        description = "Siamese cats are famous for their striking blue eyes and distinctive color points.",
        lifespanLow = 12,
        lifespanHigh = 20,
        isFavorite = false
    )

    val breeds = listOf(persianBreed, maineCoonBreed, siameseBreed)

    val favoriteBreeds = breeds.filter { it.isFavorite }
}
