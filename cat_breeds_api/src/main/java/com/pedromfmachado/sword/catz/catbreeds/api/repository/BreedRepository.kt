package com.pedromfmachado.sword.catz.catbreeds.api.repository

import com.pedromfmachado.sword.catz.catbreeds.api.model.Breed

interface BreedRepository {
    fun getBreeds(): List<Breed>
    fun getFavoriteBreeds(): List<Breed>
    fun getBreedById(id: String): Breed?
}
