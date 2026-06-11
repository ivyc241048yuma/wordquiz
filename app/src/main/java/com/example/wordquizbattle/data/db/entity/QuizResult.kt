package com.example.wordquizbattle.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_results",
    foreignKeys = [ForeignKey(
        entity = Deck::class,
        parentColumns = ["id"],
        childColumns = ["deckId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val mode: String,
    val score: Int = 0,
    val maxCombo: Int = 0,
    val scoreMultiplier: Float = 1.0f,
    val totalQuestions: Int,
    val correctAnswers: Int = 0,
    val timeTakenMs: Long? = null,
    val playedAt: Long = System.currentTimeMillis()
)