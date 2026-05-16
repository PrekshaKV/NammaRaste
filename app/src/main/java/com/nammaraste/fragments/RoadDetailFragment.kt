package com.nammaraste.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammaraste.R
import com.nammaraste.adapters.DamageReportAdapter
import com.nammaraste.databinding.FragmentRoadDetailBinding
import com.nammaraste.utils.HealthScoreCalculator
import com.nammaraste.viewmodels.RoadDirectoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoadDetailFragment : Fragment() {

    private var _binding: FragmentRoadDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoadDirectoryViewModel by viewModels()
    private lateinit var reportAdapter: DamageReportAdapter
    private var roadId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRoadDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roadId = arguments?.getInt("roadId", -1) ?: -1

        reportAdapter = DamageReportAdapter()
        binding.rvDamageReports.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }

        loadRoadData()
    }

    private fun loadRoadData() {
        val road = viewModel.getRoadById(roadId) ?: return

        binding.tvRoadName.text = road.name
        binding.tvRoadLocation.text = road.location
        binding.tvHealthScore.text = road.healthScore.toString()
        binding.tvHealthLabel.text = HealthScoreCalculator.getHealthLabel(road.healthScore)
        binding.tvRoadType.text = road.roadType
        binding.tvRoadLength.text = "${road.lengthKm} km"
        binding.tvReportCount.text = road.totalReports.toString()
        binding.tvRoadDescription.text = road.description
        binding.tvContractorName.text = road.contractorName

        // Health circle color
        val healthColor = Color.parseColor(HealthScoreCalculator.getHealthColor(road.healthScore))
        val circleBg = binding.viewHealthCircle.background
        if (circleBg is GradientDrawable) {
            circleBg.setColor(healthColor)
        } else {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(healthColor)
            binding.viewHealthCircle.background = shape
        }

        // Warranty info
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        try {
            val builtDate = dateFormat.parse(road.builtDate)
            val warrantyEnd = dateFormat.parse(road.warrantyEndDate)
            val now = Date()

            binding.tvBuiltDate.text = builtDate?.let { displayFormat.format(it) }
            binding.tvWarrantyEnd.text = warrantyEnd?.let { displayFormat.format(it) }

            if (builtDate != null && warrantyEnd != null) {
                val totalDays = ((warrantyEnd.time - builtDate.time) / (1000 * 60 * 60 * 24)).toInt()
                val elapsedDays = ((now.time - builtDate.time) / (1000 * 60 * 60 * 24)).toInt()
                val progress = ((elapsedDays.toFloat() / totalDays) * 100).toInt().coerceIn(0, 100)
                binding.progressWarranty.progress = progress

                if (now.before(warrantyEnd)) {
                    binding.tvWarrantyStatus.text = "Under Warranty"
                    binding.tvWarrantyStatus.setTextColor(Color.parseColor("#4CAF50"))
                } else {
                    binding.tvWarrantyStatus.text = "Warranty Expired"
                    binding.tvWarrantyStatus.setTextColor(Color.parseColor("#F44336"))
                }
            }
        } catch (e: Exception) {
            binding.tvBuiltDate.text = road.builtDate
            binding.tvWarrantyEnd.text = road.warrantyEndDate
        }

        // Contractor click
        binding.cardContractor.setOnClickListener {
            val bundle = Bundle().apply { putInt("contractorId", road.contractorId) }
            findNavController().navigate(R.id.action_roadDetail_to_contractor, bundle)
        }

        // Report damage button
        binding.btnReportDamage.setOnClickListener {
            val bundle = Bundle().apply { putInt("roadId", road.id) }
            findNavController().navigate(R.id.action_roadDetail_to_report, bundle)
        }

        // Load damage reports
        val reports = viewModel.getReportsForRoad(roadId)
        if (reports.isEmpty()) {
            binding.tvNoReports.visibility = View.VISIBLE
            binding.rvDamageReports.visibility = View.GONE
        } else {
            binding.tvNoReports.visibility = View.GONE
            binding.rvDamageReports.visibility = View.VISIBLE
            reportAdapter.submitList(reports.sortedByDescending { it.timestamp })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
