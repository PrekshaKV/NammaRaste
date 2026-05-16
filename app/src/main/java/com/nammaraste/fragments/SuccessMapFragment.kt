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
import com.nammaraste.adapters.BestRoadAdapter
import com.nammaraste.databinding.FragmentSuccessMapBinding
import com.nammaraste.viewmodels.DashboardViewModel

class SuccessMapFragment : Fragment() {

    private var _binding: FragmentSuccessMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var bestRoadAdapter: BestRoadAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSuccessMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bestRoadAdapter = BestRoadAdapter { road ->
            val bundle = Bundle().apply { putInt("roadId", road.id) }
            findNavController().navigate(R.id.action_success_to_roadDetail, bundle)
        }

        binding.rvBestRoads.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bestRoadAdapter
        }

        viewModel.roads.observe(viewLifecycleOwner) { roads ->
            val sortedRoads = roads.sortedByDescending { it.healthScore }
            bestRoadAdapter.submitList(sortedRoads)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
