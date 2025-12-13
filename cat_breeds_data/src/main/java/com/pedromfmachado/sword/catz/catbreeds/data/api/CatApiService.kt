package com.pedromfmachado.sword.catz.catbreeds.data.api

import com.pedromfmachado.sword.catz.catbreeds.data.api.dto.BreedDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApiService {

    @GET("breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 100
    ): List<BreedDto>

    @GET("breeds/{id}")
    suspend fun getBreedById(
        @Path("id") id: String
    ): BreedDto
}
