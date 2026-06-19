package com.example.wordquizbattle.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.data.db.entity.QuizResult
import com.example.wordquizbattle.databinding.ItemRankingBinding

class RankingAdapter : ListAdapter<QuizResult, RankingAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: QuizResult, rank: Int) {
            binding.tvRank.text = "$rank"
            binding.tvRankingScore.text = "Score: ${result.score}"
            binding.tvRankingCombo.text = "Combo: ${result.maxCombo}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    class DiffCallback : DiffUtil.ItemCallback<QuizResult>() {
        override fun areItemsTheSame(oldItem: QuizResult, newItem: QuizResult) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: QuizResult, newItem: QuizResult) = oldItem == newItem
    }
}