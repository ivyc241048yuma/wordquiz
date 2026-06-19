package com.example.wordquizbattle.data.db.dao

import androidx.room.*
import com.example.wordquizbattle.data.db.entity.QuizAnswerLog

@Dao
interface QuizAnswerLogDao {
    @Query("SELECT * FROM quiz_answer_logs WHERE quizResultId = :resultId")
    suspend fun getLogsByResult(resultId: Long): List<QuizAnswerLog>

    @Query("""
        SELECT wordId,
               SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) AS correct,
               COUNT(*) AS total
        FROM quiz_answer_logs
        GROUP BY wordId
        ORDER BY CAST(correct AS REAL) / total ASC
    """)
    suspend fun getWordAccuracyStats(): List<WordAccuracy>

    @Insert
    suspend fun insertLog(log: QuizAnswerLog)

    @Insert
    suspend fun insertLogs(logs: List<QuizAnswerLog>)
}

data class WordAccuracy(val wordId: Long, val correct: Int, val total: Int)