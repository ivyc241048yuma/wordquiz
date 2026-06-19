package com.example.wordquizbattle.data.repository

import com.example.wordquizbattle.data.db.dao.QuizAnswerLogDao
import com.example.wordquizbattle.data.db.entity.QuizAnswerLog

class QuizAnswerLogRepository(private val dao: QuizAnswerLogDao) {
    suspend fun insertLogs(logs: List<QuizAnswerLog>) = dao.insertLogs(logs)
    suspend fun getLogsByResult(resultId: Long) = dao.getLogsByResult(resultId)
}