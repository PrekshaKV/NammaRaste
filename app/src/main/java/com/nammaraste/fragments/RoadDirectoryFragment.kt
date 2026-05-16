package com.nammaraste.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammaraste.R
import com.nammaraste.adapters.FilterChipAdapter
import com.nammaraste.adapters.RoadAdapter
import com.nammaraste.databinding.FragmentRoadDirectoryBinding
import com.nammaraste.viewmodels.RoadDirectoryViewModel

class RoadDirectoryFragment : Fragment() {

    private var _binding: FragmentRoadDirectoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoadDirectoryViewModel by viewModels()
    private lateinit var roadAdapter: RoadAdapter
    private lateinit var filterAdapter: FilterChipAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRoadDirectoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupSearch()
        observeData()
    }

    private fun setupAdapters() {
        roadAdapter = RoadAdapter { road ->
            val bundle = Bundle().apply { putInt("roadId", road.id) }
            findNavController().navigate(R.id.action_directory_to_roadDetail, bundle)
        }
        binding.rvRoads.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = roadAdapter
        }

        filterAdapter = FilterChipAdapter { type ->
            viewModel.filterByType(type)
        }
        binding.rvFilters.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchRoads(text?.toString() ?: "")
        }
    }

    private fun observeData() {
        viewModel.filteredRoads.observe(viewLifecycleOwner) { roads ->
            roadAdapter.submitList(roads)
        }
        viewModel.roadTypes.observe(viewLifecycleOwner) { types ->
            filterAdapter.submitList(types)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
