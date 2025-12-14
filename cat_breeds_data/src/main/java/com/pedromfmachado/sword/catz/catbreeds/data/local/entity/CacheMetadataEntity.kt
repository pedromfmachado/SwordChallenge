package com.pedromfmachado.sword.catz.catbreeds.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_metadata")
data class CacheMetadataEntity(
    @PrimaryKey val cacheKey: String,
    val lastFetchedAt: Long,
    val expiresAt: Long
)
