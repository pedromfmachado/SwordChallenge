package com.pedromfmachado.sword.catz.catbreeds.data.mapper

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import javax.inject.Inject

class BreedRemoteMapper
    @Inject
    constructor() {
        fun mapToDomain(dto: BreedDto): Breed {
            val (low, high) = parseLifespan(dto.lifeSpan)
            return Breed(
                id = dto.id,
                name = dto.name,
                imageUrl = dto.image?.url ?: "",
                origin = dto.origin,
                temperament = dto.temperament,
                description = dto.description,
                lifespanLow = low,
                lifespanHigh = high,
                isFavorite = false,
            )
        }

        fun mapToDomain(dtos: List<BreedDto>): List<Breed> = dtos.map { mapToDomain(it) }

        private fun parseLifespan(lifespan: String): Pair<Int, Int> {
            val numbers = Regex("\\d+").findAll(lifespan)
                .map { it.value.toInt() }
                .toList()
            return Pair(
                numbers.getOrNull(0) ?: 0,
                numbers.getOrNull(1) ?: numbers.getOrNull(0) ?: 0,
            )
        }
    }
