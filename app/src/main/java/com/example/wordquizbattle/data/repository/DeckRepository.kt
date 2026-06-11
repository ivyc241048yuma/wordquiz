package com.example.wordquizbattle.data.repository

import com.example.wordquizbattle.data.db.dao.DeckDao
import com.example.wordquizbattle.data.db.entity.Deck

class DeckRepository(private val deckDao: DeckDao) {
    val allDecks = deckDao.getAllDecks()

    suspend fun insert(deck: Deck) = deckDao.insertDeck(deck)
    suspend fun update(deck: Deck) = deckDao.updateDeck(deck)
    suspend fun delete(deck: Deck) = deckDao.deleteDeck(deck)
    suspend fun getDeckById(id: Long) = deckDao.getDeckById(id)
}