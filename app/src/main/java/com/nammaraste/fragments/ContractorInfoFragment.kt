package com.nammaraste.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nammaraste.databinding.FragmentContractorInfoBinding
import com.nammaraste.viewmodels.RoadDirectoryViewModel

class ContractorInfoFragment : Fragment() {

    private var _binding: FragmentContractorInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoadDirectoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContractorInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contractorId = arguments?.getInt("contractorId", -1) ?: -1
        loadContractorData(contractorId)
    }

    private fun loadContractorData(contractorId: Int) {
        val contractor = viewModel.getContractorById(contractorId) ?: return

        binding.tvContractorName.text = contractor.name
        binding.tvCompanyName.text = contractor.company
        binding.tvSpecialization.text = contractor.specialization
        binding.tvRoadsBuilt.text = contractor.roadsBuilt.toString()
        binding.tvRating.text = contractor.rating.toString()
        binding.tvPhone.text = contractor.phone
        binding.tvEmail.text = contractor.email
        binding.tvAddress.text = contractor.address

        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contractor.phone}")
            startActivity(intent)
        }

        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:${contractor.email}")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Road Maintenance Query")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
