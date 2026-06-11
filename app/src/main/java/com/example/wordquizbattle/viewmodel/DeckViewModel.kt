package com.example.wordquizbattle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.data.db.entity.Deck
import com.example.wordquizbattle.data.repository.DeckRepository
import kotlinx.coroutines.launch

class DeckViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: DeckRepository
    val allDecks = run {
        val db = AppDatabase.getDatabase(application)
        repo = DeckRepository(db.deckDao())
        repo.allDecks
    }

    fun addDeck(name: String, description: String?, colorHex: String) {
        viewModelScope.launch {
            repo.insert(Deck(name = name, description = description, colorHex = colorHex))
        }
    }

    fun deleteDeck(deck: Deck) = viewModelScope.launch { repo.delete(deck) }
    fun updateDeck(deck: Deck) = viewModelScope.launch { repo.update(deck) }
}