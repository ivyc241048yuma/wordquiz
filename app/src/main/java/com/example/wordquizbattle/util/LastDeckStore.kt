package com.example.wordquizbattle.util

import android.content.Context

object LastDeckStore {
    private const val PREF_NAME = "word_quiz_battle_prefs"
    private const val KEY_LAST_DECK_ID = "last_deck_id"

    fun saveLastDeckId(context: Context, deckId: Long) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_DECK_ID, deckId).apply()
    }

    fun getLastDeckId(context: Context): Long? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getLong(KEY_LAST_DECK_ID, -1L)
        return if (id == -1L) null else id
    }
}