package com.example.wordquizbattle.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.wordquizbattle.R
import com.example.wordquizbattle.databinding.FragmentModeSelectBinding
import com.example.wordquizbattle.util.LastDeckStore
import com.example.wordquizbattle.viewmodel.DeckViewModel
import com.example.wordquizbattle.viewmodel.WordViewModel
import com.google.android.material.card.MaterialCardView

class ModeSelectFragment : Fragment() {
    private var _binding: FragmentModeSelectBinding? = null
    private val binding get() = _binding!!
    private val deckViewModel: DeckViewModel by viewModels()
    private val wordViewModel: WordViewModel by viewModels()
    private var deckId: Long = 0

    // デフォルトは「4択クイズ」を選択済みにしておく（画像の初期状態に合わせる）
    private var selectedMode = "quiz"

    // カード本体・タイトル・説明文・モードIDをひとまとめにして扱うための入れ物
    private data class ModeCard(
        val card: MaterialCardView,
        val title: TextView,
        val desc: TextView,
        val mode: String
    )

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
        // カード・タイトル・説明文・モードIDをセットで管理する
        val modeCards = listOf(
            ModeCard(binding.cardQuizMode, binding.tvQuizTitle, binding.tvQuizDesc, "quiz"),
            ModeCard(binding.cardTimeAttackMode, binding.tvTimeAttackTitle, binding.tvTimeAttackDesc, "timeattack"),
            ModeCard(binding.cardComboMode, binding.tvComboTitle, binding.tvComboDesc, "combo"),
            ModeCard(binding.cardWeakMode, binding.tvWeakTitle, binding.tvWeakDesc, "weak")
        )

        modeCards.forEach { modeCard ->
            modeCard.card.setOnClickListener {
                selectedMode = modeCard.mode
                updateModeSelectionUi(modeCards)
            }
        }

        updateModeSelectionUi(modeCards)
    }

    private fun updateModeSelectionUi(modeCards: List<ModeCard>) {
        val context = requireContext()
        // 枠線の太さ（2dp相当をpxに変換）
        val strokeWidthPx = (2 * context.resources.displayMetrics.density).toInt()
        val strokeColor = ContextCompat.getColor(context, R.color.main_purple)

        modeCards.forEach { modeCard ->
            val isSelected = modeCard.mode == selectedMode

            // 背景：選択中=薄紫／非選択=統一グレー
            modeCard.card.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.light_purple_bg else R.color.input_field_bg
                )
            )

            // 枠線：選択中のみ紫の線を表示（非選択は0dpで非表示）
            modeCard.card.strokeWidth = if (isSelected) strokeWidthPx else 0
            modeCard.card.strokeColor = strokeColor

            // タイトル・説明文の文字色：選択中は紫、非選択は元の黒・グレー
            val titleColor = ContextCompat.getColor(
                context,
                if (isSelected) R.color.main_purple else R.color.text_primary
            )
            val descColor = ContextCompat.getColor(
                context,
                if (isSelected) R.color.main_purple else R.color.text_secondary
            )
            modeCard.title.setTextColor(titleColor)
            modeCard.desc.setTextColor(descColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}