package com.example.wordquizbattle.ui.word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.databinding.FragmentWordRegisterBinding
import com.example.wordquizbattle.viewmodel.WordViewModel

class WordRegisterFragment : Fragment() {
    private var _binding: FragmentWordRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private var deckId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deckId = arguments?.getLong("deckId") ?: 0

        binding.btnSaveWord.setOnClickListener {
            val term = binding.etTerm.text.toString().trim()
            val definition = binding.etDefinition.text.toString().trim()

            if (term.isEmpty()) {
                binding.tilTerm.error = "単語を入力してください"
                return@setOnClickListener
            }
            if (definition.isEmpty()) {
                binding.tilDefinition.error = "意味を入力してください"
                return@setOnClickListener
            }

            viewModel.addWord(
                deckId = deckId,
                term = term,
                definition = definition,
                exampleSent = binding.etExample.text.toString().trim().ifEmpty { null },
                memo = binding.etMemo.text.toString().trim().ifEmpty { null }
            )
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}