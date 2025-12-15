package com.pedromfmachado.sword.catz.catbreeds.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pedromfmachado.sword.catz.catbreeds.data.local.entity.CacheMetadataEntity

@Dao
interface CacheMetadataDao {
    @Query("SELECT * FROM cache_metadata WHERE cacheKey = :key")
    suspend fun getCacheMetadata(key: String): CacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheMetadata(metadata: CacheMetadataEntity)
}
