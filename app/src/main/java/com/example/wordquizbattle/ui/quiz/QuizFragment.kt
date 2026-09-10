package com.example.wordquizbattle.ui.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.data.db.entity.QuizResult
import com.example.wordquizbattle.data.db.entity.Word
import com.example.wordquizbattle.data.repository.QuizAnswerLogRepository
import com.example.wordquizbattle.data.repository.QuizResultRepository
import com.example.wordquizbattle.data.repository.WordRepository
import com.example.wordquizbattle.databinding.FragmentQuizBinding
import com.example.wordquizbattle.viewmodel.QuizViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()

    private var deckId: Long = 0
    private var mode: String = "quiz"
    private var questionStartTime: Long = 0
    private var timeLeftMs = 60_000L
    private var countDownTimer: CountDownTimer? = null

    // 正解/不正解のハイライトを見せている間、次の問題に進むまでの待ち時間
    private val answerRevealDelayMs = 700L
    private val choiceLetters = listOf("A", "B", "C", "D")

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
            val questionCount = if (mode == "combo") 50 else 10
            quizViewModel.loadQuestions(deckId, mode, questionCount)
            showQuestion()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showQuestion() {
        if (quizViewModel.isFinished()) {
            saveResultAndNavigate()
            return
        }
        val word = quizViewModel.currentWord() ?: return
        binding.tvQuestion.text = word.term

        binding.tvProgress.text = if (mode == "combo") {
            "${quizViewModel.currentIndex + 1}問目"
        } else {
            "${quizViewModel.currentIndex + 1} / ${quizViewModel.questionCount()}問"
        }

        updateProgressBar()
        updateScoreUi()

        val choices = quizViewModel.generateChoices(word)
        val correctIndex = choices.indexOf(word.definition)
        val buttons = listOf(binding.btnChoice0, binding.btnChoice1, binding.btnChoice2, binding.btnChoice3)

        buttons.forEachIndexed { i, button ->
            resetChoiceStyle(button)
            button.text = "${choiceLetters[i]}.${choices[i]}"
            button.isEnabled = true
            button.setOnClickListener {
                onChoiceSelected(i, correctIndex, choices[i], word, buttons)
            }
        }
        questionStartTime = System.currentTimeMillis()
    }

    private fun onChoiceSelected(
        selectedIndex: Int,
        correctIndex: Int,
        selectedChoice: String,
        word: Word,
        buttons: List<Button>
    ) {
        // 連打防止：判定〜次の問題に進むまではすべて無効化
        buttons.forEach { it.isEnabled = false }

        val context = requireContext()
        buttons[correctIndex].background = ContextCompat.getDrawable(context, R.drawable.bg_choice_correct)
        buttons[correctIndex].setTextColor(ContextCompat.getColor(context, R.color.success_green))

        if (selectedIndex != correctIndex) {
            buttons[selectedIndex].background = ContextCompat.getDrawable(context, R.drawable.bg_choice_wrong)
            buttons[selectedIndex].setTextColor(ContextCompat.getColor(context, R.color.danger_red))
        }

        val timeTaken = System.currentTimeMillis() - questionStartTime

        // ハイライトを少し見せてから次の問題へ
        view?.postDelayed({
            if (_binding == null) return@postDelayed
            quizViewModel.answer(selectedChoice, word.definition, word, timeTaken)
            showQuestion()
        }, answerRevealDelayMs)
    }

    private fun resetChoiceStyle(button: Button) {
        val context = requireContext()
        button.background = ContextCompat.getDrawable(context, R.drawable.bg_input_field)
        button.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
    }

    private fun updateProgressBar() {
        binding.progressQuiz.max = quizViewModel.questionCount()
        binding.progressQuiz.progress = quizViewModel.currentIndex
    }

    private fun updateScoreUi() {
        val numberFormat = NumberFormat.getNumberInstance(Locale.JAPAN)
        binding.tvScoreValue.text = numberFormat.format(quizViewModel.score)
        binding.tvMultiplierValue.text =
            String.format(Locale.JAPAN, "×%.1f", quizViewModel.scoreMultiplier)

        if (quizViewModel.currentCombo > 0) {
            binding.tvComboBadge.visibility = View.VISIBLE
            binding.tvComboBadge.text = "×${quizViewModel.currentCombo} コンボ"
        } else {
            binding.tvComboBadge.visibility = View.GONE
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMs, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMs = millisUntilFinished
                val sec = millisUntilFinished / 1000
                binding.tvTimer.text = "$sec"
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
                totalQuestions = if (mode == "combo") quizViewModel.currentIndex else quizViewModel.questionCount(),
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

            // 結果画面で「もう一度チャレンジ」を実現するため、deckId/modeも一緒に渡す
            val bundle = Bundle().apply {
                putLong("resultId", resultId)
                putLong("deckId", deckId)
                putString("mode", mode)
            }
            findNavController().navigate(R.id.toResult, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}