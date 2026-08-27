package com.example.wordquizbattle.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.databinding.ItemRankingBinding

data class RankingItem(
    val deckName: String,
    val score: Int,
    val maxCombo: Int,
    val rank: Int
)

class RankingAdapter : ListAdapter<RankingItem, RankingAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemRankingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RankingItem) {
            binding.tvRank.text = "${item.rank}"
            binding.tvDeckName.text = item.deckName
            binding.tvRankingCombo.text = "最大コンボ ×${item.maxCombo}"
            binding.tvRankingScore.text = "${item.score}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<RankingItem>() {
        override fun areItemsTheSame(oldItem: RankingItem, newItem: RankingItem) =
            oldItem.rank == newItem.rank
        override fun areContentsTheSame(oldItem: RankingItem, newItem: RankingItem) =
            oldItem == newItem
    }
}