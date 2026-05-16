package com.nammaraste.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaraste.databinding.ItemRoadBinding
import com.nammaraste.models.Road
import com.nammaraste.utils.HealthScoreCalculator

class RoadAdapter(
    private val onRoadClick: (Road) -> Unit
) : ListAdapter<Road, RoadAdapter.RoadViewHolder>(RoadDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadViewHolder {
        val binding = ItemRoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoadViewHolder(private val binding: ItemRoadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(road: Road) {
            binding.tvRoadName.text = road.name
            binding.tvLocation.text = road.location
            binding.tvHealthScore.text = road.healthScore.toString()
            binding.tvRoadType.text = road.roadType
            binding.tvLength.text = "${road.lengthKm} km"
            binding.tvReports.text = "${road.totalReports} reports"

            val healthColor = Color.parseColor(HealthScoreCalculator.getHealthColor(road.healthScore))
            val bg = binding.viewHealthIndicator.background
            if (bg is GradientDrawable) {
                bg.setColor(healthColor)
            } else {
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.OVAL
                shape.setColor(healthColor)
                binding.viewHealthIndicator.background = shape
            }

            binding.root.setOnClickListener { onRoadClick(road) }
        }
    }

    class RoadDiffCallback : DiffUtil.ItemCallback<Road>() {
        override fun areItemsTheSame(oldItem: Road, newItem: Road) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Road, newItem: Road) = oldItem == newItem
    }
}
