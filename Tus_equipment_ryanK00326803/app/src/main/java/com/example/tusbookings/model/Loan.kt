package com.example.tusbookings.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "loans",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.RESTRICT
    )],
    indices = [Index(value = ["itemId"]), Index(value = ["userUid"])]
)
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val userUid: String,
    val units: Int,
    val returnBy: String,
    val reason: String,
    val returned: Boolean = false
)

// Result of a JOIN, not a separate Room table.
data class LoanSummary(
    val id: Int,
    val itemId: Int,
    val name: String,
    val units: Int,
    val returnBy: String,
    val reason: String,
    val returned: Boolean
)
