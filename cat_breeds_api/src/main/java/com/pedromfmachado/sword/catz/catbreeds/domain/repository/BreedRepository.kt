package com.pedromfmachado.sword.catz.catbreeds.domain.repository

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed

interface BreedRepository {
    fun getBreeds(): List<Breed>
    fun getFavoriteBreeds(): List<Breed>
    fun getBreedById(id: String): Breed?
}
