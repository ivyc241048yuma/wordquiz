package com.example.wordquizbattle.ui.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentAnalysisBinding
import kotlinx.coroutines.launch

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

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

            val items = stats.map { stat ->
                val word = db.wordDao().getWordById(stat.wordId)
                val accuracy = if (stat.total > 0) (stat.correct * 100 / stat.total) else 0
                WeakWordItem(word?.term ?: "不明", accuracy)
            }
            adapter.submitList(items)

            val overall = if (items.isNotEmpty())
                items.map { it.accuracy }.average().toInt()
            else 0
            binding.tvOverallAccuracy.text = "全体正解率: $overall%"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}