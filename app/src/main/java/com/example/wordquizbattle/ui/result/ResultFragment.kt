package com.example.wordquizbattle.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentResultBinding
import kotlinx.coroutines.launch

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private var resultId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resultId = arguments?.getLong("resultId") ?: 0
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val logs = db.quizAnswerLogDao().getLogsByResult(resultId)
            val correct = logs.count { it.isCorrect }
            val total = logs.size
            val rate = if (total > 0) (correct * 100 / total) else 0

            binding.tvCorrectRate.text = "正解率: $correct / $total ($rate%)"
        }

        binding.btnViewAnalysis.setOnClickListener {
            findNavController().navigate(R.id.toAnalysis)
        }

        binding.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}