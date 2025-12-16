package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import javax.inject.Inject

class CalculateAverageLifespanUseCase
    @Inject
    constructor() {
        operator fun invoke(breeds: List<Breed>): Int? {
            return breeds.takeIf { it.isNotEmpty() }
                ?.map { (it.lifespanLow + it.lifespanHigh) / 2.0 }
                ?.average()
                ?.toInt()
        }
    }
