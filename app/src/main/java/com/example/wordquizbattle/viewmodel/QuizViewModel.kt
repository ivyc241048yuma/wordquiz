package com.example.wordquizbattle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.data.db.entity.QuizAnswerLog
import com.example.wordquizbattle.data.db.entity.Word
import com.example.wordquizbattle.data.repository.WordRepository

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val wordRepo = WordRepository(AppDatabase.getDatabase(application).wordDao())

    private var questionList: List<Word> = emptyList()
    private var allWords: List<Word> = emptyList()
    var currentIndex = 0
    var score = 0
    var currentCombo = 0
    var maxCombo = 0
    var scoreMultiplier = 1.0f
    val answerLogs = mutableListOf<QuizAnswerLog>()

    suspend fun loadQuestions(deckId: Long, mode: String, count: Int) {
        allWords = wordRepo.getRandomWords(deckId, 100)
        questionList = when (mode) {
            "weak" -> wordRepo.getWeakWords(deckId, count)
            else -> wordRepo.getRandomWords(deckId, count)
        }
    }

    fun currentWord() = questionList.getOrNull(currentIndex)

    fun generateChoices(correct: Word): List<String> {
        val wrongs = allWords
            .filter { it.id != correct.id }
            .shuffled()
            .take(3)
            .map { it.definition }
        return (wrongs + correct.definition).shuffled()
    }

    fun answer(selectedDef: String, correctDef: String, word: Word, timeTakenMs: Long): Boolean {
        val isCorrect = selectedDef == correctDef
        if (isCorrect) {
            currentCombo++
            if (currentCombo > maxCombo) maxCombo = currentCombo
            scoreMultiplier = 1.0f + (currentCombo / 5) * 0.5f
            score += (100 * scoreMultiplier).toInt()
        } else {
            currentCombo = 0
            scoreMultiplier = 1.0f
        }
        answerLogs.add(
            QuizAnswerLog(
                quizResultId = 0,
                wordId = word.id,
                isCorrect = isCorrect,
                selectedAnswer = selectedDef,
                timeTakenMs = timeTakenMs
            )
        )
        currentIndex++
        return isCorrect
    }

    fun isFinished() = currentIndex >= questionList.size

    fun questionCount() = questionList.size
}