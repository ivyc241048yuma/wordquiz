package com.example.wordquizbattle.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.databinding.FragmentModeSelectBinding
import com.example.wordquizbattle.util.LastDeckStore
import com.example.wordquizbattle.viewmodel.DeckViewModel
import com.example.wordquizbattle.viewmodel.WordViewModel

class ModeSelectFragment : Fragment() {
    private var _binding: FragmentModeSelectBinding? = null
    private val binding get() = _binding!!
    private val deckViewModel: DeckViewModel by viewModels()
    private val wordViewModel: WordViewModel by viewModels()
    private var deckId: Long = 0

    // デフォルトは「4択クイズ」を選択済みにしておく（画像の初期状態に合わせる）
    private var selectedMode = "quiz"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deckId = arguments?.getLong("deckId") ?: 0
        LastDeckStore.saveLastDeckId(requireContext(), deckId)

        setupDeckInfo()
        setupModePicker()

        binding.btnStartMode.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("deckId", deckId)
                putString("mode", selectedMode)
            }
            findNavController().navigate(R.id.quizFragment, bundle)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupDeckInfo() {
        deckViewModel.loadDeckById(deckId)
        deckViewModel.selectedDeck.observe(viewLifecycleOwner) { deck ->
            updateDeckInfoText(deck?.name)
        }
        wordViewModel.getWordsByDeck(deckId).observe(viewLifecycleOwner) { words ->
            updateDeckInfoText(deckViewModel.selectedDeck.value?.name, words.size)
        }
    }

    private fun updateDeckInfoText(deckName: String?, wordCount: Int? = null) {
        val name = deckName ?: return
        val count = wordCount ?: 0
        binding.tvDeckInfo.text = "$name　用語・${count}語"
    }

    private fun setupModePicker() {
        val modeMap = mapOf(
            binding.cardQuizMode to "quiz",
            binding.cardTimeAttackMode to "timeattack",
            binding.cardComboMode to "combo",
            binding.cardWeakMode to "weak"
        )

        modeMap.keys.forEach { card ->
            card.setOnClickListener {
                selectedMode = modeMap[card] ?: selectedMode
                updateModeSelectionUi(modeMap)
            }
        }

        updateModeSelectionUi(modeMap)
    }

    private fun updateModeSelectionUi(modeMap: Map<CardView, String>) {
        modeMap.forEach { (card, mode) ->
            val isSelected = mode == selectedMode
            card.setCardBackgroundColor(
                android.graphics.Color.parseColor(if (isSelected) "#EDE7F6" else "#FFFFFF")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}