package com.example.wordquizbattle.ui.analysis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.databinding.ItemWeakWordBinding

data class WeakWordItem(val term: String, val accuracy: Int)

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
            binding.tvWeakAccuracy.text = "${item.accuracy}%"
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