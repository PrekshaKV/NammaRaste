package com.nammaraste.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nammaraste.R
import com.nammaraste.adapters.DamageReportAdapter
import com.nammaraste.databinding.FragmentDamageReportBinding
import com.nammaraste.viewmodels.DamageReportViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DamageReportFragment : Fragment() {

    private var _binding: FragmentDamageReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DamageReportViewModel by viewModels()
    private lateinit var reportAdapter: DamageReportAdapter

    private var currentPhotoPath: String = ""
    private var capturedLatitude: Double = 0.0
    private var capturedLongitude: Double = 0.0
    private var locationCaptured = false
    private var preselectedRoadId: Int = -1

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            binding.ivPhotoPreview.visibility = View.VISIBLE
            binding.ivPhotoPreview.setImageURI(Uri.fromFile(File(currentPhotoPath)))
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) takePhoto()
    }

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.values.any { it }) captureLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDamageReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preselectedRoadId = arguments?.getInt("roadId", -1) ?: -1

        setupUI()
        setupListeners()
        observeData()
    }

    private fun setupUI() {
        reportAdapter = DamageReportAdapter()
        binding.rvAllReports.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }

        // Default severity
        binding.chipMedium.isChecked = true
    }

    private fun setupListeners() {
        binding.btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnCaptureLocation.setOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (permissions.any { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
                captureLocation()
            } else {
                locationPermissionLauncher.launch(permissions)
            }
        }

        binding.btnSubmitReport.setOnClickListener {
            submitReport()
        }
    }

    private fun observeData() {
        viewModel.roads.observe(viewLifecycleOwner) { roads ->
            val roadNames = roads.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roadNames)
            binding.actvRoadName.setAdapter(adapter)

            // Pre-select road if navigated from road detail
            if (preselectedRoadId != -1) {
                val road = roads.find { it.id == preselectedRoadId }
                road?.let { binding.actvRoadName.setText(it.name, false) }
            }
        }

        viewModel.allReports.observe(viewLifecycleOwner) { reports ->
            reportAdapter.submitList(reports.take(10))
        }

        viewModel.reportSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Snackbar.make(binding.root, getString(R.string.report_submitted), Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.health_excellent))
                    .show()
                clearForm()
            }
        }
    }

    private fun takePhoto() {
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = File(requireContext().filesDir, "damage_photos")
        if (!dir.exists()) dir.mkdirs()
        return File.createTempFile("DAMAGE_${timestamp}_", ".jpg", dir)
    }

    private fun captureLocation() {
        try {
            val locationManager = requireContext().getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                val lastKnown = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)

                if (lastKnown != null) {
                    capturedLatitude = lastKnown.latitude
                    capturedLongitude = lastKnown.longitude
                    locationCaptured = true
                    binding.tvLocationInfo.text = "Lat: %.6f, Lng: %.6f".format(capturedLatitude, capturedLongitude)
                    binding.tvLocationInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.health_excellent))
                } else {
                    // Simulate GPS for demo
                    capturedLatitude = 12.3047 + (Math.random() * 0.01)
                    capturedLongitude = 76.2929 + (Math.random() * 0.01)
                    locationCaptured = true
                    binding.tvLocationInfo.text = "Lat: %.6f, Lng: %.6f (Simulated)".format(capturedLatitude, capturedLongitude)
                    binding.tvLocationInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
                }
            }
        } catch (e: Exception) {
            // Simulate for demo
            capturedLatitude = 12.3047 + (Math.random() * 0.01)
            capturedLongitude = 76.2929 + (Math.random() * 0.01)
            locationCaptured = true
            binding.tvLocationInfo.text = "Lat: %.6f, Lng: %.6f (Simulated)".format(capturedLatitude, capturedLongitude)
            binding.tvLocationInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        }
    }

    private fun submitReport() {
        val roadName = binding.actvRoadName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val reporterName = binding.etReporterName.text.toString().trim()

        if (roadName.isEmpty()) {
            binding.tilRoadName.error = "Please select a road"
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "Please describe the damage"
            return
        }
        if (reporterName.isEmpty()) {
            binding.etReporterName.error = "Please enter your name"
            return
        }

        val road = viewModel.getRoadByName(roadName)
        if (road == null) {
            binding.tilRoadName.error = "Road not found"
            return
        }

        val severity = when {
            binding.chipCritical.isChecked -> "Critical"
            binding.chipHigh.isChecked -> "High"
            binding.chipMedium.isChecked -> "Medium"
            binding.chipLow.isChecked -> "Low"
            else -> "Medium"
        }

        if (!locationCaptured) {
            capturedLatitude = road.latitude + (Math.random() * 0.005)
            capturedLongitude = road.longitude + (Math.random() * 0.005)
        }

        viewModel.submitReport(
            roadId = road.id,
            roadName = road.name,
            description = description,
            severity = severity,
            photoPath = currentPhotoPath,
            latitude = capturedLatitude,
            longitude = capturedLongitude,
            reporterName = reporterName
        )
    }

    private fun clearForm() {
        binding.actvRoadName.text?.clear()
        binding.etDescription.text?.clear()
        binding.etReporterName.text?.clear()
        binding.chipMedium.isChecked = true
        binding.ivPhotoPreview.visibility = View.GONE
        binding.tvLocationInfo.text = "Location not captured"
        binding.tvLocationInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        currentPhotoPath = ""
        locationCaptured = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
