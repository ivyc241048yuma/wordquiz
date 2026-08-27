package com.example.wordquizbattle.ui.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordquizbattle.R
import com.example.wordquizbattle.databinding.FragmentDeckListBinding
import com.example.wordquizbattle.viewmodel.DeckViewModel

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

        viewModel.allDecks.observe(viewLifecycleOwner) { decks ->
            adapter.submitList(decks)
        }

        binding.cardAddDeck.setOnClickListener {
            findNavController().navigate(R.id.toDeckCreate)
        }

        binding.btnStartDeck.setOnClickListener {
            val deckId = selectedDeckId
            if (deckId == null) {
                Toast.makeText(requireContext(), "デッキを選択してください", Toast.LENGTH_SHORT).show()
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