package com.pedromfmachado.sword.catz.catbreeds.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.BreedDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.dao.FavoriteDao
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.BreedEntity
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.FavoriteEntity

@Database(
    entities = [BreedEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CatBreedsDatabase : RoomDatabase() {
    abstract fun breedDao(): BreedDao

    abstract fun favoriteDao(): FavoriteDao
}
