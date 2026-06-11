package com.example.wordquizbattle.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val colorHex: String = "#845ef7",
    val createdAt: Long = System.currentTimeMillis()
)