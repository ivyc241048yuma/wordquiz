package com.example.wordquizbattle.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wordquizbattle.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardQuiz.setOnClickListener {
            // TODO: デッキ選択画面へ遷移
        }
        binding.cardTimeAttack.setOnClickListener {
            // TODO: デッキ選択画面へ遷移（タイムアタックモード）
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}