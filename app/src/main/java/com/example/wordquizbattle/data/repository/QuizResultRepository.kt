package com.example.wordquizbattle.data.repository

import com.example.wordquizbattle.data.db.dao.QuizResultDao
import com.example.wordquizbattle.data.db.entity.QuizResult

class QuizResultRepository(private val dao: QuizResultDao) {
    fun getRanking(deckId: Long, mode: String) = dao.getRanking(deckId, mode)
    fun getGlobalRanking(mode: String) = dao.getGlobalRanking(mode)
    suspend fun insert(result: QuizResult) = dao.insertResult(result)
}