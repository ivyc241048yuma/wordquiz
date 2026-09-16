package com.example.wordquizbattle.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentResultBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private var resultId: Long = 0
    private var deckId: Long = 0
    private var mode: String = "quiz"

    private val wrongWordAdapter = WrongWordAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resultId = arguments?.getLong("resultId") ?: 0
        deckId = arguments?.getLong("deckId") ?: 0
        mode = arguments?.getString("mode") ?: "quiz"

        binding.rvWrongWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWrongWords.adapter = wrongWordAdapter

        loadResult()

        binding.btnRetryQuiz.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("deckId", deckId)
                putString("mode", mode)
            }
            findNavController().navigate(R.id.quizFragment, bundle)
        }

        binding.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun loadResult() {
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val result = db.quizResultDao().getResultById(resultId) ?: return@launch
            val logs = db.quizAnswerLogDao().getLogsByResult(resultId)

            val correct = result.correctAnswers
            val total = result.totalQuestions
            val rate = if (total > 0) (correct * 100 / total) else 0

            val timeLogs = logs.mapNotNull { it.timeTakenMs }
            val avgTimeSec = if (timeLogs.isNotEmpty()) (timeLogs.average() / 1000) else 0.0

            val bestScore = db.quizResultDao().getBestScore(deckId, mode, resultId) ?: 0
            val isNewBest = result.score > bestScore

            val numberFormat = NumberFormat.getNumberInstance(Locale.JAPAN)
            binding.tvFinalScore.text = numberFormat.format(result.score)
            binding.tvMaxComboValue.text = "×${result.maxCombo}"
            binding.tvNewBest.visibility = if (isNewBest) View.VISIBLE else View.GONE

            binding.tvCorrectCount.text = "$correct / $total"
            binding.tvAccuracyValue.text = "$rate%"
            binding.tvAvgTimeValue.text = "${avgTimeSec.toInt()}s"
            binding.tvMaxMultiplierValue.text = "×${result.scoreMultiplier}"

            val wrongLogs = logs.filter { !it.isCorrect }
            val wrongWords = wrongLogs.mapNotNull { log ->
                db.wordDao().getWordById(log.wordId)?.let { word ->
                    WrongWordItem(term = word.term, definition = word.definition)
                }
            }
            wrongWordAdapter.submitList(wrongWords)
            binding.rvWrongWords.visibility = if (wrongWords.isEmpty()) View.GONE else View.VISIBLE
            binding.tvNoWrongWords.visibility = if (wrongWords.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}