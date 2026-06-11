package com.example.wordquizbattle.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wordquizbattle.data.db.entity.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE deckId = :deckId AND isActive = 1 ORDER BY createdAt DESC")
    fun getWordsByDeck(deckId: Long): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE deckId = :deckId AND isActive = 1 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(deckId: Long, limit: Int): List<Word>

    @Query("""
        SELECT * FROM words WHERE deckId = :deckId AND isActive = 1
        AND (correctCount + missCount) > 0
        ORDER BY CAST(correctCount AS REAL) / (correctCount + missCount) ASC
        LIMIT :limit
    """)
    suspend fun getWeakWords(deckId: Long, limit: Int): List<Word>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Insert
    suspend fun insertWord(word: Word): Long

    @Update
    suspend fun updateWord(word: Word)

    @Query("UPDATE words SET isActive = 0 WHERE id = :id")
    suspend fun deactivateWord(id: Long)
}