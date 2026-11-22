package com.example.tusbookings.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [ForeignKey(
        entity = ItemType::class,
        parentColumns = ["id"],
        childColumns = ["typeId"],
        onDelete = ForeignKey.RESTRICT
    )],
    indices = [Index(value = ["typeId"])]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val typeId: Int,
    val name: String,
    val description: String,
    val location: String,
    val condition: String,
    val unitsAvailable: Int,
    val unitsTotal: Int
)
