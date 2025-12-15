package com.pedromfmachado.sword.catz.catbreeds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.BreedEntity

@Dao
interface BreedDao {
    @Query("SELECT * FROM breeds")
    suspend fun getAllBreeds(): List<BreedEntity>

    @Query("SELECT * FROM breeds WHERE id = :id")
    suspend fun getBreedById(id: String): BreedEntity?

    @Query("SELECT * FROM breeds WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchBreeds(query: String): List<BreedEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<BreedEntity>)

    @Query("DELETE FROM breeds")
    suspend fun deleteAllBreeds()
}
