package com.pedromfmachado.sword.catz.catbreeds.data.mock

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed

object MockBreedData {
    val breeds = listOf(
        Breed(
            id = "1",
            name = "Persian",
            imageUrl = "https://cdn2.thecatapi.com/images/OGTWqNNOt.jpg",
            origin = "Iran (Persia)",
            temperament = "Affectionate, Loyal, Quiet, Gentle",
            description = "Persians are known for their long, luxurious coats and sweet, gentle personalities. They are calm, affectionate cats that enjoy a peaceful environment and make excellent lap cats.",
            lifespanLow = 12,
            lifespanHigh = 17,
            isFavorite = false
        ),
        Breed(
            id = "2",
            name = "Maine Coon",
            imageUrl = "https://cdn2.thecatapi.com/images/OOD3VXAQn.jpg",
            origin = "United States",
            temperament = "Adaptable, Intelligent, Playful, Gentle",
            description = "Maine Coons are one of the largest domestic cat breeds, known for their friendly, sociable nature. They are excellent hunters and love to play, yet are gentle giants who get along well with children and other pets.",
            lifespanLow = 12,
            lifespanHigh = 15,
            isFavorite = true
        ),
        Breed(
            id = "3",
            name = "Siamese",
            imageUrl = "https://cdn2.thecatapi.com/images/ai6Jps4sx.jpg",
            origin = "Thailand",
            temperament = "Active, Vocal, Social, Intelligent",
            description = "Siamese cats are famous for their striking blue eyes and distinctive color points. They are highly social, vocal cats that form strong bonds with their owners and love to communicate through their unique meows.",
            lifespanLow = 12,
            lifespanHigh = 20,
            isFavorite = false
        ),
        Breed(
            id = "4",
            name = "British Shorthair",
            imageUrl = "https://cdn2.thecatapi.com/images/s4wQfYoEk.jpg",
            origin = "United Kingdom",
            temperament = "Calm, Easygoing, Loyal, Affectionate",
            description = "British Shorthairs are known for their round faces, dense coats, and calm demeanor. They are independent yet affectionate cats that adapt well to indoor living and make wonderful companions.",
            lifespanLow = 12,
            lifespanHigh = 20,
            isFavorite = true
        ),
        Breed(
            id = "5",
            name = "Ragdoll",
            imageUrl = "https://cdn2.thecatapi.com/images/oGefY4YoG.jpg",
            origin = "United States",
            temperament = "Docile, Calm, Affectionate, Friendly",
            description = "Ragdolls are large, gentle cats known for going limp when picked up, hence their name. They are extremely affectionate, often following their owners around the house and greeting them at the door.",
            lifespanLow = 12,
            lifespanHigh = 17,
            isFavorite = false
        ),
        Breed(
            id = "6",
            name = "Bengal",
            imageUrl = "https://cdn2.thecatapi.com/images/O3btzLlsO.png",
            origin = "United States",
            temperament = "Alert, Agile, Energetic, Curious",
            description = "Bengals are known for their wild appearance with distinctive spotted or marbled coats. They are highly active, intelligent cats that love to climb, play, and explore their surroundings.",
            lifespanLow = 12,
            lifespanHigh = 16,
            isFavorite = true
        ),
        Breed(
            id = "7",
            name = "Abyssinian",
            imageUrl = "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            origin = "Egypt",
            temperament = "Active, Curious, Intelligent, Playful",
            description = "Abyssinians are one of the oldest known cat breeds, with a distinctive ticked coat. They are highly active, curious cats that love to explore and are known for their dog-like loyalty to their owners.",
            lifespanLow = 9,
            lifespanHigh = 15,
            isFavorite = false
        ),
        Breed(
            id = "8",
            name = "Scottish Fold",
            imageUrl = "https://cdn2.thecatapi.com/images/o9t0LDcsa.jpg",
            origin = "Scotland",
            temperament = "Sweet, Adaptable, Loving, Playful",
            description = "Scottish Folds are famous for their unique folded ears that give them an owl-like appearance. They are sweet-natured, adaptable cats that enjoy being around people and other pets.",
            lifespanLow = 11,
            lifespanHigh = 14,
            isFavorite = false
        )
    )

    val favoriteBreeds: List<Breed>
        get() = breeds.filter { it.isFavorite }

    fun findBreedById(id: String): Breed? = breeds.find { it.id == id }
}
