package com.example.wordquizbattle.ui.word

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.data.db.entity.Word
import com.example.wordquizbattle.databinding.ItemWordBinding

class WordAdapter(
    private val onDeleteClick: (Word) -> Unit
) : ListAdapter<Word, WordAdapter.WordViewHolder>(DiffCallback()) {

    inner class WordViewHolder(private val binding: ItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.tvTerm.text = word.term
            binding.tvDefinition.text = word.definition

            val total = word.correctCount + word.missCount
            if (total == 0) {
                // 未出題：グレーで「-」表示
                binding.progressAccuracy.progress = 0
                binding.progressAccuracy.progressTintList =
                    ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
                binding.tvAccuracy.text = "-"
                binding.tvAccuracy.setTextColor(Color.parseColor("#888888"))
            } else {
                val accuracy = (word.correctCount * 100) / total
                val color = if (accuracy >= 60) {
                    Color.parseColor("#4CAF50") // 緑（得意）
                } else {
                    Color.parseColor("#F44336") // 赤（苦手）
                }
                binding.progressAccuracy.progress = accuracy
                binding.progressAccuracy.progressTintList = ColorStateList.valueOf(color)
                binding.tvAccuracy.text = "$accuracy%"
                binding.tvAccuracy.setTextColor(color)
            }

            binding.btnDeleteWord.setOnClickListener { onDeleteClick(word) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
}