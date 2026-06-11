package com.example.wordquizbattle.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordquizbattle.data.db.entity.QuizResult

@Dao
interface QuizResultDao {
    @Query("SELECT * FROM quiz_results WHERE deckId = :deckId AND mode = :mode ORDER BY score DESC LIMIT 20")
    fun getRanking(deckId: Long, mode: String): LiveData<List<QuizResult>>

    @Query("SELECT * FROM quiz_results WHERE mode = :mode ORDER BY score DESC LIMIT 20")
    fun getGlobalRanking(mode: String): LiveData<List<QuizResult>>

    @Insert
    suspend fun insertResult(result: QuizResult): Long
}