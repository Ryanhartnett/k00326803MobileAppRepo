package com.example.tusbookings.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_types")
data class ItemType(
    @PrimaryKey val id: Int,
    val name: String
)
