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

    // 弱点分析画面の「苦手単語だけクイズする」用：デッキを問わず苦手な単語を集める
    @Query("""
        SELECT * FROM words WHERE isActive = 1
        AND (correctCount + missCount) > 0
        ORDER BY CAST(correctCount AS REAL) / (correctCount + missCount) ASC
        LIMIT :limit
    """)
    suspend fun getWeakWordsAllDecks(limit: Int): List<Word>

    // 上記の選択肢（不正解の選択肢）生成用：デッキを問わずランダムに取得
    @Query("SELECT * FROM words WHERE isActive = 1 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWordsAllDecks(limit: Int): List<Word>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Insert
    suspend fun insertWord(word: Word): Long

    @Update
    suspend fun updateWord(word: Word)

    @Query("UPDATE words SET isActive = 0 WHERE id = :id")
    suspend fun deactivateWord(id: Long)

    @Query("SELECT COUNT(*) FROM words WHERE isActive = 1")
    suspend fun getTotalWordCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE deckId = :deckId AND isActive = 1")
    suspend fun getWordCountByDeck(deckId: Long): Int
}