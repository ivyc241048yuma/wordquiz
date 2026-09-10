package com.example.wordquizbattle.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.databinding.ItemWrongWordBinding

data class WrongWordItem(val term: String, val definition: String)

class WrongWordAdapter : RecyclerView.Adapter<WrongWordAdapter.ViewHolder>() {
    private var items: List<WrongWordItem> = emptyList()

    fun submitList(list: List<WrongWordItem>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemWrongWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WrongWordItem) {
            binding.tvWrongTerm.text = item.term
            binding.tvWrongDefinition.text = item.definition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWrongWordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}