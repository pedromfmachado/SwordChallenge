package com.pedromfmachado.sword.catz.catbreeds.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val lifespanLow: Int,
    val lifespanHigh: Int,
)
