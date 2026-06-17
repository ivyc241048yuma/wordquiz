package com.example.wordquizbattle.ui.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DeckAdapter(
            onItemClick = { deck ->
                val bundle = android.os.Bundle().apply {
                    putLong("deckId", deck.id)
                }
                findNavController().navigate(R.id.wordListFragment, bundle)
            },
            onDeleteClick = { deck ->
                viewModel.deleteDeck(deck)
            }
        )
        binding.rvDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDecks.adapter = adapter

        viewModel.allDecks.observe(viewLifecycleOwner) { decks ->
            adapter.submitList(decks)
        }

        binding.fabAddDeck.setOnClickListener {
            findNavController().navigate(R.id.toDeckCreate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}