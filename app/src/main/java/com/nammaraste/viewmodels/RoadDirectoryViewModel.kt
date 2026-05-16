package com.nammaraste.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nammaraste.models.Road
import com.nammaraste.models.Contractor
import com.nammaraste.utils.JsonDataManager
import com.nammaraste.utils.HealthScoreCalculator

class RoadDirectoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _allRoads = MutableLiveData<List<Road>>()

    private val _filteredRoads = MutableLiveData<List<Road>>()
    val filteredRoads: LiveData<List<Road>> = _filteredRoads

    private val _contractors = MutableLiveData<List<Contractor>>()
    val contractors: LiveData<List<Contractor>> = _contractors

    private val _roadTypes = MutableLiveData<List<String>>()
    val roadTypes: LiveData<List<String>> = _roadTypes

    private var currentQuery = ""
    private var currentFilter = "All"

    init {
        loadData()
    }

    private fun loadData() {
        val context = getApplication<Application>()
        val roadsJson = JsonDataManager.readJsonArrayFromAssets(context, "roads.json")
        val roadsList = mutableListOf<Road>()

        for (i in 0 until roadsJson.length()) {
            val obj = roadsJson.getJSONObject(i)
            val reportCount = JsonDataManager.getReportCountForRoad(context, obj.getInt("id"))
            val dynamicScore = HealthScoreCalculator.calculateHealthScore(
                obj.getInt("healthScore"), reportCount, obj.getDouble("lengthKm")
            )
            roadsList.add(
                Road(
                    id = obj.getInt("id"),
                    name = obj.getString("name"),
                    location = obj.getString("location"),
                    taluka = obj.getString("taluka"),
                    lengthKm = obj.getDouble("lengthKm"),
                    contractorId = obj.getInt("contractorId"),
                    contractorName = obj.getString("contractorName"),
                    builtDate = obj.getString("builtDate"),
                    warrantyEndDate = obj.getString("warrantyEndDate"),
                    roadType = obj.getString("roadType"),
                    healthScore = dynamicScore,
                    totalReports = reportCount,
                    description = obj.getString("description"),
                    latitude = obj.getDouble("latitude"),
                    longitude = obj.getDouble("longitude")
                )
            )
        }

        _allRoads.value = roadsList
        _filteredRoads.value = roadsList

        val types = roadsList.map { it.roadType }.distinct().sorted()
        _roadTypes.value = listOf("All") + types

        // Load contractors
        val contractorsJson = JsonDataManager.readJsonArrayFromAssets(context, "contractors.json")
        val contractorsList = mutableListOf<Contractor>()
        for (i in 0 until contractorsJson.length()) {
            val obj = contractorsJson.getJSONObject(i)
            contractorsList.add(
                Contractor(
                    id = obj.getInt("id"),
                    name = obj.getString("name"),
                    company = obj.getString("company"),
                    phone = obj.getString("phone"),
                    email = obj.getString("email"),
                    address = obj.getString("address"),
                    roadsBuilt = obj.getInt("roadsBuilt"),
                    rating = obj.getDouble("rating").toFloat(),
                    specialization = obj.getString("specialization")
                )
            )
        }
        _contractors.value = contractorsList
    }

    fun searchRoads(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun filterByType(type: String) {
        currentFilter = type
        applyFilters()
    }

    private fun applyFilters() {
        val all = _allRoads.value ?: return
        var filtered = all

        if (currentQuery.isNotBlank()) {
            val q = currentQuery.lowercase()
            filtered = filtered.filter {
                it.name.lowercase().contains(q) ||
                it.location.lowercase().contains(q) ||
                it.taluka.lowercase().contains(q) ||
                it.contractorName.lowercase().contains(q)
            }
        }

        if (currentFilter != "All") {
            filtered = filtered.filter { it.roadType == currentFilter }
        }

        _filteredRoads.value = filtered
    }

    fun getRoadById(id: Int): Road? {
        return _allRoads.value?.find { it.id == id }
    }

    fun getContractorById(id: Int): Contractor? {
        return _contractors.value?.find { it.id == id }
    }

    fun getReportsForRoad(roadId: Int): List<com.nammaraste.models.DamageReport> {
        val context = getApplication<Application>()
        val allReports = JsonDataManager.getAllDamageReports(context)
        val reports = mutableListOf<com.nammaraste.models.DamageReport>()
        for (i in 0 until allReports.length()) {
            val obj = allReports.getJSONObject(i)
            if (obj.getInt("roadId") == roadId) {
                reports.add(
                    com.nammaraste.models.DamageReport(
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
        }
        return reports
    }
}
