package com.pedromfmachado.sword.catz.catbreeds.data.api

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApiService {

    @GET("breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE,
        @Query("page") page: Int = 0
    ): List<BreedDto>

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    @GET("breeds/{id}")
    suspend fun getBreedById(
        @Path("id") id: String
    ): BreedDto
}
