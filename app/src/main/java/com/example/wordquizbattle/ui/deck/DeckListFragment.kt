package com.example.wordquizbattle.ui.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.AppDatabase
import com.example.wordquizbattle.databinding.FragmentDeckListBinding
import com.example.wordquizbattle.viewmodel.DeckViewModel
import kotlinx.coroutines.launch

class DeckListFragment : Fragment() {

    private var _binding: FragmentDeckListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeckViewModel by viewModels()
    private var selectedDeckId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DeckAdapter(
            onSingleClick = { deck ->
                selectedDeckId = deck.id
                (binding.rvDeckList.adapter as DeckAdapter).setSelectedDeck(deck.id)
            },
            onDoubleClick = { deck ->
                val bundle = Bundle().apply { putLong("deckId", deck.id) }
                findNavController().navigate(R.id.toWordList, bundle)
            },
            onDeleteClick = { deck ->
                if (selectedDeckId == deck.id) selectedDeckId = null
                viewModel.deleteDeck(deck)
            }
        )
        binding.rvDeckList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeckList.adapter = adapter

        val db = AppDatabase.getDatabase(requireContext())
        viewModel.allDecks.observe(viewLifecycleOwner) { decks ->
            viewLifecycleOwner.lifecycleScope.launch {
                val items = decks.map { deck ->
                    val wordCount = db.wordDao().getWordCountByDeck(deck.id)
                    val stats = db.quizAnswerLogDao().getAccuracyByDeck(deck.id)
                    val accuracy = if (stats.total > 0) (stats.correct * 100 / stats.total) else null
                    DeckWithStats(deck, wordCount, accuracy)
                }
                adapter.submitList(items)
            }
        }

        binding.cardAddDeck.setOnClickListener {
            findNavController().navigate(R.id.toDeckCreate)
        }

        binding.btnStartDeck.setOnClickListener {
            val deckId = selectedDeckId
            if (deckId == null) {
                android.widget.Toast.makeText(requireContext(), "デッキを選択してください", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply { putLong("deckId", deckId) }
            findNavController().navigate(R.id.toModeSelectFromDeck, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}