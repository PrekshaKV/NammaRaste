package com.nammaraste.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaraste.databinding.ItemBestRoadBinding
import com.nammaraste.models.Road
import com.nammaraste.utils.HealthScoreCalculator

class BestRoadAdapter(
    private val onRoadClick: (Road) -> Unit
) : ListAdapter<Road, BestRoadAdapter.BestRoadViewHolder>(BestRoadDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestRoadViewHolder {
        val binding = ItemBestRoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BestRoadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BestRoadViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class BestRoadViewHolder(private val binding: ItemBestRoadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(road: Road, rank: Int) {
            binding.tvRank.text = "#$rank"
            binding.tvRoadName.text = road.name
            binding.tvLocation.text = road.location
            binding.tvScore.text = road.healthScore.toString()
            binding.tvScoreLabel.text = HealthScoreCalculator.getHealthLabel(road.healthScore)
            binding.tvContractor.text = road.contractorName
            binding.tvReportCount.text = "${road.totalReports} report${if (road.totalReports != 1) "s" else ""}"
            binding.progressHealth.progress = road.healthScore

            val healthColor = Color.parseColor(HealthScoreCalculator.getHealthColor(road.healthScore))
            binding.tvScore.setTextColor(healthColor)
            binding.tvScoreLabel.setTextColor(healthColor)

            val rankBg = binding.tvRank.background
            if (rankBg is GradientDrawable) {
                rankBg.setColor(healthColor)
            } else {
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.OVAL
                shape.setColor(healthColor)
                binding.tvRank.background = shape
            }

            binding.root.setOnClickListener { onRoadClick(road) }
        }
    }

    class BestRoadDiffCallback : DiffUtil.ItemCallback<Road>() {
        override fun areItemsTheSame(oldItem: Road, newItem: Road) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Road, newItem: Road) = oldItem == newItem
    }
}
