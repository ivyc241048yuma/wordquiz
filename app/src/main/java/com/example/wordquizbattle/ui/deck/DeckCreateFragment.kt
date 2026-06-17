package com.example.wordquizbattle.ui.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.databinding.FragmentDeckCreateBinding
import com.example.wordquizbattle.viewmodel.DeckViewModel

class DeckCreateFragment : Fragment() {
    private var _binding: FragmentDeckCreateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeckCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnSaveDeck.setOnClickListener {
            val name = binding.etDeckName.text.toString().trim()
            val description = binding.etDeckDescription.text.toString().trim()

            if (name.isEmpty()) {
                binding.tilDeckName.error = "デッキ名を入力してください"
                return@setOnClickListener
            }

            viewModel.addDeck(
                name = name,
                description = description.ifEmpty { null },
                colorHex = "#845ef7"
            )
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}