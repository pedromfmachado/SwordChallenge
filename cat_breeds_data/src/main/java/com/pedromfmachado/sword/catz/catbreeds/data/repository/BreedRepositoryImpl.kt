package com.pedromfmachado.sword.catz.catbreeds.data.repository

import com.pedromfmachado.sword.catz.catbreeds.api.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.api.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.data.mock.MockBreedData
import javax.inject.Inject

internal class BreedRepositoryImpl @Inject constructor() : BreedRepository {

    override fun getBreeds(): List<Breed> = MockBreedData.breeds

    override fun getFavoriteBreeds(): List<Breed> = MockBreedData.breeds.filter { it.isFavorite }

    override fun getBreedById(id: String): Breed? = MockBreedData.breeds.find { it.id == id }
}
