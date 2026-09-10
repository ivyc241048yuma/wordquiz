package com.example.wordquizbattle.ui.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.databinding.ItemRankingBinding
import java.text.NumberFormat
import java.util.Locale

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
            binding.tvRankingScore.text =
                NumberFormat.getNumberInstance(Locale.JAPAN).format(item.score)

            // 1位のみ最大コンボをバッジ表示（写真デザインの「MAX×5」に合わせる）
            if (item.rank == 1 && item.maxCombo > 0) {
                binding.tvMaxComboBadge.visibility = View.VISIBLE
                binding.tvMaxComboBadge.text = "MAX×${item.maxCombo}"
            } else {
                binding.tvMaxComboBadge.visibility = View.GONE
            }
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