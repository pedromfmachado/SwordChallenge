package com.pedromfmachado.sword.catz.catbreeds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT breedId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE breedId = :breedId")
    suspend fun removeFavorite(breedId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE breedId = :breedId)")
    suspend fun isFavorite(breedId: String): Boolean
}
