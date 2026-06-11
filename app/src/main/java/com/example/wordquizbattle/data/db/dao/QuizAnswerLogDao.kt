package com.example.wordquizbattle.data.db.dao

import androidx.room.*
import com.example.wordquizbattle.data.db.entity.QuizAnswerLog

@Dao
interface QuizAnswerLogDao {
    @Query("SELECT * FROM quiz_answer_logs WHERE quizResultId = :resultId")
    suspend fun getLogsByResult(resultId: Long): List<QuizAnswerLog>

    @Insert
    suspend fun insertLog(log: QuizAnswerLog)

    @Insert
    suspend fun insertLogs(logs: List<QuizAnswerLog>)
}