package com.example.wordquizbattle.ui.quiz

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.data.db.entity.QuizResult
import com.example.wordquizbattle.data.repository.QuizAnswerLogRepository
import com.example.wordquizbattle.data.repository.QuizResultRepository
import com.example.wordquizbattle.data.repository.WordRepository
import com.example.wordquizbattle.databinding.FragmentQuizBinding
import com.example.wordquizbattle.viewmodel.QuizViewModel
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()

    private var deckId: Long = 0
    private var mode: String = "quiz"
    private var questionStartTime: Long = 0
    private var timeLeftMs = 60_000L
    private var countDownTimer: CountDownTimer? = null

    private lateinit var wordRepo: WordRepository
    private lateinit var resultRepo: QuizResultRepository
    private lateinit var logRepo: QuizAnswerLogRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deckId = arguments?.getLong("deckId") ?: 0
        mode = arguments?.getString("mode") ?: "quiz"

        val db = AppDatabase.getDatabase(requireContext())
        wordRepo = WordRepository(db.wordDao())
        resultRepo = QuizResultRepository(db.quizResultDao())
        logRepo = QuizAnswerLogRepository(db.quizAnswerLogDao())

        if (mode == "timeattack") {
            binding.tvTimer.visibility = View.VISIBLE
            startTimer()
        } else {
            binding.tvTimer.visibility = View.GONE
        }

        lifecycleScope.launch {
            quizViewModel.loadQuestions(deckId, mode, 10)
            showQuestion()
        }
    }

    private fun showQuestion() {
        if (quizViewModel.isFinished()) {
            saveResultAndNavigate()
            return
        }
        val word = quizViewModel.currentWord() ?: return
        binding.tvQuestion.text = word.term
        binding.tvProgress.text = "${quizViewModel.currentIndex + 1} / ${quizViewModel.questionCount()}"
        binding.tvScore.text = "Score: ${quizViewModel.score}  Combo: ${quizViewModel.currentCombo}"

        val choices = quizViewModel.generateChoices(word)
        val buttons = listOf(binding.btnChoice0, binding.btnChoice1, binding.btnChoice2, binding.btnChoice3)
        choices.forEachIndexed { i, choice ->
            buttons[i].text = choice
            buttons[i].setOnClickListener {
                val timeTaken = System.currentTimeMillis() - questionStartTime
                quizViewModel.answer(choice, word.definition, word, timeTaken)
                showQuestion()
            }
        }
        questionStartTime = System.currentTimeMillis()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMs, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMs = millisUntilFinished
                val sec = millisUntilFinished / 1000
                binding.tvTimer.text = "${sec}秒"
                if (sec <= 10) binding.tvTimer.setTextColor(Color.RED)
            }

            override fun onFinish() {
                saveResultAndNavigate()
            }
        }.start()
    }

    private fun saveResultAndNavigate() {
        countDownTimer?.cancel()
        lifecycleScope.launch {
            val result = QuizResult(
                deckId = deckId,
                mode = mode,
                score = quizViewModel.score,
                maxCombo = quizViewModel.maxCombo,
                scoreMultiplier = quizViewModel.scoreMultiplier,
                totalQuestions = quizViewModel.questionCount(),
                correctAnswers = quizViewModel.answerLogs.count { it.isCorrect },
                timeTakenMs = if (mode == "timeattack") 60000L - timeLeftMs else null
            )
            val resultId = resultRepo.insert(result)
            val logs = quizViewModel.answerLogs.map { it.copy(quizResultId = resultId) }
            logRepo.insertLogs(logs)

            quizViewModel.answerLogs.forEach { log ->
                val word = wordRepo.getById(log.wordId) ?: return@forEach
                val updated = if (log.isCorrect)
                    word.copy(correctCount = word.correctCount + 1, lastAnswerAt = System.currentTimeMillis())
                else
                    word.copy(missCount = word.missCount + 1, lastAnswerAt = System.currentTimeMillis())
                wordRepo.update(updated)
            }

            val bundle = Bundle().apply { putLong("resultId", resultId) }
            findNavController().navigate(R.id.toResult, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}