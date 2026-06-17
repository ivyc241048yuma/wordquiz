package com.example.wordquizbattle.ui.deck

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.data.db.entity.Deck
import com.example.wordquizbattle.databinding.ItemDeckBinding

class DeckAdapter(
    private val onItemClick: (Deck) -> Unit,
    private val onDeleteClick: (Deck) -> Unit
) : ListAdapter<Deck, DeckAdapter.DeckViewHolder>(DiffCallback()) {

    inner class DeckViewHolder(private val binding: ItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deck: Deck) {
            binding.tvDeckName.text = deck.name
            binding.tvDeckDescription.text = deck.description ?: ""
            try {
                binding.viewColor.setBackgroundColor(Color.parseColor(deck.colorHex))
            } catch (e: Exception) {
                binding.viewColor.setBackgroundColor(Color.parseColor("#845ef7"))
            }
            binding.root.setOnClickListener { onItemClick(deck) }
            binding.btnDeleteDeck.setOnClickListener { onDeleteClick(deck) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemDeckBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DeckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Deck>() {
        override fun areItemsTheSame(oldItem: Deck, newItem: Deck) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Deck, newItem: Deck) = oldItem == newItem
    }
}