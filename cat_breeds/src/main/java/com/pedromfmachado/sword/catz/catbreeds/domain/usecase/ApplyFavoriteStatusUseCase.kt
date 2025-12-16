package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import javax.inject.Inject

class ApplyFavoriteStatusUseCase
    @Inject
    constructor() {
        operator fun invoke(
            breeds: List<Breed>,
            favoriteIds: Set<String>,
        ): List<Breed> {
            return breeds.map { breed ->
                breed.copy(isFavorite = breed.id in favoriteIds)
            }
        }
    }
