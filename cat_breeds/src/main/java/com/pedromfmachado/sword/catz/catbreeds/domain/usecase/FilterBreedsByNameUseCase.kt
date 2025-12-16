package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import javax.inject.Inject

class FilterBreedsByNameUseCase
    @Inject
    constructor() {
        operator fun invoke(
            breeds: List<Breed>,
            query: String,
        ): List<Breed> {
            if (query.isBlank()) {
                return breeds
            }
            return breeds.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
