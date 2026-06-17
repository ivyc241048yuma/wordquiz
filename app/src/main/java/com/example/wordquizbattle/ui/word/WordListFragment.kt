package com.example.wordquizbattle.ui.word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.R
import com.example.wordquizbattle.databinding.FragmentWordListBinding
import com.example.wordquizbattle.viewmodel.WordViewModel

class WordListFragment : Fragment() {
    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private var deckId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deckId = arguments?.getLong("deckId") ?: 0

        val adapter = WordAdapter(
            onDeleteClick = { word -> viewModel.deleteWord(word.id) }
        )
        binding.rvWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWords.adapter = adapter

        viewModel.getWordsByDeck(deckId).observe(viewLifecycleOwner) { words ->
            adapter.submitList(words)
        }

        binding.fabAddWord.setOnClickListener {
            val bundle = Bundle().apply { putLong("deckId", deckId) }
            findNavController().navigate(R.id.wordRegisterFragment, bundle)
        }

        binding.fabStartQuiz.setOnClickListener {
            val bundle = Bundle().apply { putLong("deckId", deckId) }
            findNavController().navigate(R.id.modeSelectFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}