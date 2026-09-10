package com.example.wordquizbattle.ui.word

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.entity.Word
import com.example.wordquizbattle.databinding.FragmentWordListBinding
import com.example.wordquizbattle.viewmodel.WordViewModel

class WordListFragment : Fragment() {
    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WordViewModel by viewModels()
    private var deckId: Long = 0

    private var allWords: List<Word> = emptyList()
    private var searchQuery: String = ""
    private var currentFilter: String = "all"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deckId = arguments?.getLong("deckId") ?: 0

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = WordAdapter(
            onDeleteClick = { word -> viewModel.deleteWord(word.id) }
        )
        binding.rvWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWords.adapter = adapter

        viewModel.getWordsByDeck(deckId).observe(viewLifecycleOwner) { words ->
            allWords = words
            applyFilter(adapter)
        }

        setupSearch(adapter)
        setupFilterTabs(adapter)

        binding.fabAddWord.setOnClickListener {
            val bundle = Bundle().apply { putLong("deckId", deckId) }
            findNavController().navigate(R.id.wordRegisterFragment, bundle)
        }

        binding.fabStartQuiz.setOnClickListener {
            val bundle = Bundle().apply { putLong("deckId", deckId) }
            findNavController().navigate(R.id.modeSelectFragment, bundle)
        }
    }

    private fun setupSearch(adapter: WordAdapter) {
        binding.etSearchWord.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                applyFilter(adapter)
            }
        })
    }

    private fun setupFilterTabs(adapter: WordAdapter) {
        val tabMap = mapOf(
            binding.tabAll to "all",
            binding.tabWeak to "weak",
            binding.tabUnseen to "unseen"
        )

        tabMap.keys.forEach { tab ->
            tab.setOnClickListener {
                currentFilter = tabMap[tab] ?: currentFilter
                updateTabUi(tabMap)
                applyFilter(adapter)
            }
        }
        updateTabUi(tabMap)
    }

    private fun updateTabUi(tabMap: Map<android.widget.TextView, String>) {
        tabMap.forEach { (tab, filter) ->
            val isSelected = filter == currentFilter
            tab.setBackgroundResource(
                if (isSelected) R.drawable.tab_selected else R.drawable.tab_unselected
            )
            tab.setTextColor(
                android.graphics.Color.parseColor(if (isSelected) "#FFFFFF" else "#888888")
            )
        }
    }

    private fun applyFilter(adapter: WordAdapter) {
        val filtered = allWords
            .filter { word ->
                searchQuery.isBlank() ||
                        word.term.contains(searchQuery, ignoreCase = true) ||
                        word.definition.contains(searchQuery, ignoreCase = true)
            }
            .filter { word ->
                val total = word.correctCount + word.missCount
                when (currentFilter) {
                    "weak" -> total > 0 && (word.correctCount * 100 / total) < 60
                    "unseen" -> total == 0
                    else -> true
                }
            }
        adapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}