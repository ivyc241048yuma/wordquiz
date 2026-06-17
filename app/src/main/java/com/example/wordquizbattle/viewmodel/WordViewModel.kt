package com.example.wordquizbattle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.data.db.entity.Word
import com.example.wordquizbattle.data.repository.WordRepository
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = WordRepository(AppDatabase.getDatabase(application).wordDao())

    fun getWordsByDeck(deckId: Long) = repo.getWordsByDeck(deckId)

    fun addWord(
        deckId: Long,
        term: String,
        definition: String,
        exampleSent: String?,
        memo: String?
    ) {
        viewModelScope.launch {
            repo.insert(
                Word(
                    deckId = deckId,
                    term = term,
                    definition = definition,
                    exampleSent = exampleSent,
                    memo = memo
                )
            )
        }
    }

    fun deleteWord(id: Long) = viewModelScope.launch { repo.deactivate(id) }
}