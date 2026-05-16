package com.nammaraste.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaraste.databinding.ItemDamageReportBinding
import com.nammaraste.models.DamageReport
import com.nammaraste.utils.HealthScoreCalculator
import java.text.SimpleDateFormat
import java.util.Locale

class DamageReportAdapter :
    ListAdapter<DamageReport, DamageReportAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemDamageReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(private val binding: ItemDamageReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: DamageReport) {
            binding.tvRoadName.text = report.roadName
            binding.tvDescription.text = report.description
            binding.tvSeverity.text = report.severity
            binding.tvReporter.text = "By: ${report.reporterName}"
            binding.tvStatus.text = report.status

            // Format timestamp
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                val date = inputFormat.parse(report.timestamp)
                binding.tvTimestamp.text = date?.let { outputFormat.format(it) } ?: report.timestamp
            } catch (e: Exception) {
                binding.tvTimestamp.text = report.timestamp
            }

            // Severity color
            val sevColor = Color.parseColor(HealthScoreCalculator.getSeverityColor(report.severity))
            val bg = binding.tvSeverity.background
            if (bg is GradientDrawable) {
                bg.setColor(sevColor)
            } else {
                val shape = GradientDrawable()
                shape.cornerRadius = 24f
                shape.setColor(sevColor)
                binding.tvSeverity.background = shape
            }
            binding.tvSeverity.setTextColor(Color.WHITE)

            // Status color
            when (report.status.lowercase()) {
                "pending" -> binding.tvStatus.setTextColor(Color.parseColor("#FF9800"))
                "under review" -> binding.tvStatus.setTextColor(Color.parseColor("#2196F3"))
                "resolved" -> binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
        }
    }

    class ReportDiffCallback : DiffUtil.ItemCallback<DamageReport>() {
        override fun areItemsTheSame(oldItem: DamageReport, newItem: DamageReport) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DamageReport, newItem: DamageReport) = oldItem == newItem
    }
}
