package com.example.wordquizbattle.ui.deck

import android.graphics.Color
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wordquizbattle.R
import com.example.wordquizbattle.data.db.entity.Deck
import com.example.wordquizbattle.databinding.ItemDeckBinding

data class DeckWithStats(
    val deck: Deck,
    val wordCount: Int,
    val accuracy: Int?
)

class DeckAdapter(
    private val onSingleClick: (Deck) -> Unit,
    private val onDoubleClick: (Deck) -> Unit,
    private val onDeleteClick: (Deck) -> Unit
) : ListAdapter<DeckWithStats, DeckAdapter.DeckViewHolder>(DiffCallback()) {

    private var selectedDeckId: Long? = null

    fun setSelectedDeck(deckId: Long?) {
        val oldId = selectedDeckId
        selectedDeckId = deckId
        currentList.forEachIndexed { index, item ->
            if (item.deck.id == oldId || item.deck.id == deckId) {
                notifyItemChanged(index)
            }
        }
    }

    inner class DeckViewHolder(private val binding: ItemDeckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val gestureDetector = GestureDetector(
            binding.root.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean = true

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onSingleClick(getItem(position).deck)
                    }
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onDoubleClick(getItem(position).deck)
                    }
                    return true
                }
            }
        )

        init {
            binding.root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }
        }

        fun bind(item: DeckWithStats) {
            val deck = item.deck
            binding.tvDeckName.text = deck.name
            binding.tvDeckDescription.text = deck.description ?: ""

            val accuracyText = if (item.accuracy != null) "${item.accuracy}%" else "--%"
            binding.tvDeckStats.text = binding.root.context.getString(
                R.string.deck_stats_format, item.wordCount, accuracyText
            )

            try {
                binding.viewColor.setBackgroundColor(Color.parseColor(deck.colorHex))
            } catch (e: Exception) {
                binding.viewColor.setBackgroundColor(Color.parseColor("#845ef7"))
            }

            val isSelected = deck.id == selectedDeckId
            binding.root.setCardBackgroundColor(
                Color.parseColor(if (isSelected) "#EDE7F6" else "#FFFFFF")
            )

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

    class DiffCallback : DiffUtil.ItemCallback<DeckWithStats>() {
        override fun areItemsTheSame(oldItem: DeckWithStats, newItem: DeckWithStats) =
            oldItem.deck.id == newItem.deck.id
        override fun areContentsTheSame(oldItem: DeckWithStats, newItem: DeckWithStats) =
            oldItem == newItem
    }
}