package com.example.wordquizbattle.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_answer_logs",
    foreignKeys = [
        ForeignKey(
            entity = QuizResult::class,
            parentColumns = ["id"],
            childColumns = ["quizResultId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizAnswerLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizResultId: Long,
    val wordId: Long,
    val isCorrect: Boolean,
    val selectedAnswer: String? = null,
    val timeTakenMs: Long? = null,
    val answeredAt: Long = System.currentTimeMillis()
)