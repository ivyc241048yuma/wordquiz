package com.example.wordquizbattle.data.repository

import com.example.wordquizbattle.data.db.dao.WordDao
import com.example.wordquizbattle.data.db.entity.Word

class WordRepository(private val wordDao: WordDao) {
    fun getWordsByDeck(deckId: Long) = wordDao.getWordsByDeck(deckId)
    suspend fun insert(word: Word) = wordDao.insertWord(word)
    suspend fun update(word: Word) = wordDao.updateWord(word)
    suspend fun deactivate(id: Long) = wordDao.deactivateWord(id)
    suspend fun getRandomWords(deckId: Long, n: Int) = wordDao.getRandomWords(deckId, n)
    suspend fun getWeakWords(deckId: Long, n: Int) = wordDao.getWeakWords(deckId, n)
    suspend fun getById(id: Long) = wordDao.getWordById(id)
}