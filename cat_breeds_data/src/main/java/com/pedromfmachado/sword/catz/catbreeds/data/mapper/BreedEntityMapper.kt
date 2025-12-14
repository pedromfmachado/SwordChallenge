package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.BreedEntity
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import javax.inject.Inject

class BreedEntityMapper @Inject constructor() {

    fun mapToDomain(entity: BreedEntity): Breed =
        Breed(
            id = entity.id,
            name = entity.name,
            imageUrl = entity.imageUrl,
            origin = entity.origin,
            temperament = entity.temperament,
            description = entity.description,
            lifespanLow = entity.lifespanLow,
            lifespanHigh = entity.lifespanHigh,
            isFavorite = false
        )

    fun mapToDomain(entities: List<BreedEntity>): List<Breed> =
        entities.map { mapToDomain(it) }

    fun mapToEntity(breed: Breed): BreedEntity =
        BreedEntity(
            id = breed.id,
            name = breed.name,
            imageUrl = breed.imageUrl,
            origin = breed.origin,
            temperament = breed.temperament,
            description = breed.description,
            lifespanLow = breed.lifespanLow,
            lifespanHigh = breed.lifespanHigh
        )

    fun mapToEntities(breeds: List<Breed>): List<BreedEntity> =
        breeds.map { mapToEntity(it) }
}
