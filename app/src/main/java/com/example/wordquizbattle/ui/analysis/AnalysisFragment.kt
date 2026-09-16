package com.example.wordquizbattle.ui.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentAnalysisBinding
import kotlinx.coroutines.launch

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    // 「苦手」の基準は単語一覧画面のフィルタと合わせて正答率60%未満
    private val weakAccuracyThreshold = 60

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = AnalysisAdapter()
        binding.rvWeakWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWeakWords.adapter = adapter

        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val stats = db.quizAnswerLogDao().getWordAccuracyStats()
                .filter { it.total > 0 }

            // 全体成功率は出題実績がある単語すべてから算出
            val overall = if (stats.isNotEmpty()) {
                stats.map { (it.correct * 100 / it.total) }.average().toInt()
            } else 0
            binding.tvOverallAccuracyValue.text = "$overall%"

            // 苦手ランキングは正答率60%未満のみ、正答率が低い順
            val weakItems = stats
                .filter { (it.correct * 100 / it.total) < weakAccuracyThreshold }
                .mapNotNull { stat ->
                    val word = db.wordDao().getWordById(stat.wordId) ?: return@mapNotNull null
                    val accuracy = stat.correct * 100 / stat.total
                    WeakWordItem(term = word.term, accuracy = accuracy, definition = word.definition)
                }
                .sortedBy { it.accuracy }

            adapter.submitList(weakItems)
            binding.tvWeakWordCountValue.text = "${weakItems.size}語"
            binding.btnQuizWeakWords.isEnabled = weakItems.isNotEmpty()
            binding.rvWeakWords.visibility = if (weakItems.isEmpty()) View.GONE else View.VISIBLE
            binding.tvNoWeakWords.visibility = if (weakItems.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnQuizWeakWords.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("deckId", 0L)
                putString("mode", "weak_global")
            }
            findNavController().navigate(com.example.wordquizbattle.R.id.quizFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}