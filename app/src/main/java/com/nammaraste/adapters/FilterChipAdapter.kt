package com.nammaraste.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nammaraste.R

class FilterChipAdapter(
    private val onFilterSelected: (String) -> Unit
) : RecyclerView.Adapter<FilterChipAdapter.ChipViewHolder>() {

    private var items = listOf<String>()
    private var selectedPosition = 0

    fun submitList(list: List<String>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val chip = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter_chip, parent, false) as Chip
        return ChipViewHolder(chip)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.bind(items[position], position == selectedPosition)
    }

    override fun getItemCount() = items.size

    inner class ChipViewHolder(private val chip: Chip) : RecyclerView.ViewHolder(chip) {
        fun bind(text: String, isSelected: Boolean) {
            chip.text = text
            chip.isChecked = isSelected
            chip.setOnClickListener {
                val oldPos = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                onFilterSelected(text)
            }
        }
    }
}
