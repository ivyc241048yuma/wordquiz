package com.example.wordquizbattle.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wordquizbattle.data.db.dao.*
import com.example.wordquizbattle.data.db.entity.*

@Database(
    entities = [Deck::class, Word::class, QuizResult::class, QuizAnswerLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun wordDao(): WordDao
    abstract fun quizResultDao(): QuizResultDao
    abstract fun quizAnswerLogDao(): QuizAnswerLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "word_quiz_battle.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}