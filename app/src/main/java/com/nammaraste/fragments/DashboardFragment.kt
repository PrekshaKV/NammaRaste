package com.nammaraste.fragments

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
import com.nammaraste.databinding.FragmentDashboardBinding
import com.nammaraste.models.DamageReport
import com.nammaraste.utils.JsonDataManager
import com.nammaraste.viewmodels.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var reportAdapter: DamageReportAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardData()
        loadRecentReports()
    }

    private fun setupRecyclerView() {
        reportAdapter = DamageReportAdapter()
        binding.rvRecentReports.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }
    }

    private fun observeData() {
        viewModel.totalRoads.observe(viewLifecycleOwner) {
            binding.tvTotalRoads.text = it.toString()
        }
        viewModel.averageHealth.observe(viewLifecycleOwner) {
            binding.tvAvgHealth.text = "$it%"
        }
        viewModel.totalReports.observe(viewLifecycleOwner) {
            binding.tvTotalReports.text = it.toString()
        }
        viewModel.criticalRoads.observe(viewLifecycleOwner) {
            binding.tvCriticalRoads.text = it.toString()
        }
        viewModel.excellentCount.observe(viewLifecycleOwner) {
            binding.tvExcellentCount.text = it.toString()
        }
        viewModel.goodCount.observe(viewLifecycleOwner) {
            binding.tvGoodCount.text = it.toString()
        }
        viewModel.fairCount.observe(viewLifecycleOwner) {
            binding.tvFairCount.text = it.toString()
        }
        viewModel.poorCount.observe(viewLifecycleOwner) {
            binding.tvPoorCount.text = it.toString()
        }
    }

    private fun loadRecentReports() {
        val allReportsJson = JsonDataManager.getAllDamageReports(requireContext())
        val reports = mutableListOf<DamageReport>()
        for (i in 0 until allReportsJson.length()) {
            val obj = allReportsJson.getJSONObject(i)
            reports.add(
                DamageReport(
                    id = obj.getInt("id"),
                    roadId = obj.getInt("roadId"),
                    roadName = obj.getString("roadName"),
                    description = obj.getString("description"),
                    severity = obj.getString("severity"),
                    photoPath = obj.optString("photoPath", ""),
                    latitude = obj.getDouble("latitude"),
                    longitude = obj.getDouble("longitude"),
                    timestamp = obj.getString("timestamp"),
                    reporterName = obj.getString("reporterName"),
                    status = obj.optString("status", "Pending")
                )
            )
        }
        reportAdapter.submitList(reports.sortedByDescending { it.timestamp }.take(5))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
