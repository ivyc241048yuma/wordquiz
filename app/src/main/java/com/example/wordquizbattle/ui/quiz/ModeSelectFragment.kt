package com.example.wordquizbattle.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.databinding.FragmentModeSelectBinding

class ModeSelectFragment : Fragment() {
    private var _binding: FragmentModeSelectBinding? = null
    private val binding get() = _binding!!
    private var deckId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deckId = arguments?.getLong("deckId") ?: 0

        binding.btnQuizMode.setOnClickListener { startQuiz("quiz") }
        binding.btnTimeAttackMode.setOnClickListener { startQuiz("timeattack") }
        binding.btnWeakMode.setOnClickListener { startQuiz("weak") }
    }

    private fun startQuiz(mode: String) {
        val bundle = Bundle().apply {
            putLong("deckId", deckId)
            putString("mode", mode)
        }
        findNavController().navigate(R.id.quizFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}