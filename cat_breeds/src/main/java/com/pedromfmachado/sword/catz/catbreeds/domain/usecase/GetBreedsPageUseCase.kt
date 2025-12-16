package com.pedromfmachado.sword.catz.catbreeds.domain.usecase

import com.pedromfmachado.sword.catz.catbreeds.domain.model.Breed
import com.pedromfmachado.sword.catz.catbreeds.domain.model.PageResult
import com.pedromfmachado.sword.catz.catbreeds.domain.repository.BreedRepository
import com.pedromfmachado.sword.catz.catbreeds.domain.result.Result
import javax.inject.Inject

class GetBreedsPageUseCase
    @Inject
    constructor(
        private val breedRepository: BreedRepository,
    ) {
        suspend operator fun invoke(
            page: Int,
            pageSize: Int,
        ): Result<PageResult<Breed>> {
            return when (val result = breedRepository.getBreeds(page, pageSize)) {
                is Result.Success -> {
                    val hasMorePages = result.data.size == pageSize
                    Result.Success(PageResult(result.data, hasMorePages))
                }
                is Result.Error -> result
            }
        }
    }
