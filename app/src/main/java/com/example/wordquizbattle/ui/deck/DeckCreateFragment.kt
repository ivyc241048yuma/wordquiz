package com.example.wordquizbattle.ui.deck

import android.content.res.ColorStateList
import android.graphics.Color
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

    // デフォルトはブルーを選択中にしておく（プレビューの初期表示と合わせる）
    private var selectedColorHex = "#3F51B5"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeckCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupColorPicker()

        binding.btnCreateDeck.setOnClickListener {
            val name = binding.etDeckName.text.toString().trim()
            val description = binding.etDeckDescription.text.toString().trim()

            if (name.isEmpty()) {
                binding.etDeckName.error = "デッキ名を入力してください"
                return@setOnClickListener
            }

            viewModel.addDeck(
                name = name,
                description = description.ifEmpty { null },
                colorHex = selectedColorHex
            )
            findNavController().popBackStack()
        }
    }

    private fun setupColorPicker() {
        val colorMap = mapOf(
            binding.colorBlue to "#3F51B5",
            binding.colorGreen to "#4CAF50",
            binding.colorOrange to "#FF9800",
            binding.colorRed to "#F44336",
            binding.colorMint to "#4DB6AC"
        )

        colorMap.keys.forEach { colorView ->
            colorView.setOnClickListener {
                selectedColorHex = colorMap[colorView] ?: selectedColorHex
                updateColorSelectionUi(colorMap)
            }
        }

        // 初期表示（デフォルト選択色をUIに反映）
        updateColorSelectionUi(colorMap)
    }

    private fun updateColorSelectionUi(colorMap: Map<View, String>) {
        colorMap.forEach { (colorView, hex) ->
            val isSelected = hex == selectedColorHex
            colorView.scaleX = if (isSelected) 1.25f else 1f
            colorView.scaleY = if (isSelected) 1.25f else 1f
            colorView.elevation = if (isSelected) 6f else 0f
        }

        // プレビューカードの色ドットにも選択中の色を反映
        binding.viewPreviewColor.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(selectedColorHex))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}