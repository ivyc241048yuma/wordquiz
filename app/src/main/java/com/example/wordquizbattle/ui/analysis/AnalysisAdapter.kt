package com.example.wordquizbattle.ui.analysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.databinding.ItemWeakWordBinding

data class WeakWordItem(
    val term: String,
    val accuracy: Int,
    val definition: String? = null
)

class AnalysisAdapter : RecyclerView.Adapter<AnalysisAdapter.ViewHolder>() {
    private var items: List<WeakWordItem> = emptyList()

    fun submitList(list: List<WeakWordItem>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemWeakWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeakWordItem) {
            binding.tvWeakTerm.text = item.term
            binding.progressWeakAccuracy.max = 100
            binding.progressWeakAccuracy.progress = item.accuracy

            if (item.definition.isNullOrBlank()) {
                // ランキング画面TOP3：意味なし・パーセントのみ
                binding.tvWeakDefinition.visibility = View.GONE
                binding.tvWeakAccuracy.text = "${item.accuracy}%"
            } else {
                // 弱点分析画面：意味あり・「◯%正解」表記
                binding.tvWeakDefinition.visibility = View.VISIBLE
                binding.tvWeakDefinition.text = item.definition
                binding.tvWeakAccuracy.text = "${item.accuracy}%正解"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWeakWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}