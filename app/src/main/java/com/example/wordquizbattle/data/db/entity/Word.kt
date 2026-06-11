package com.example.wordquizbattle.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["id"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val term: String,
    val definition: String,
    val exampleSent: String? = null,
    val memo: String? = null,
    val correctCount: Int = 0,
    val missCount: Int = 0,
    val isActive: Boolean = true,
    val lastAnswerAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)