package com.nammaraste.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nammaraste.models.DamageReport
import com.nammaraste.models.Road
import com.nammaraste.utils.JsonDataManager
import com.nammaraste.utils.HealthScoreCalculator
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DamageReportViewModel(application: Application) : AndroidViewModel(application) {

    private val _roads = MutableLiveData<List<Road>>()
    val roads: LiveData<List<Road>> = _roads

    private val _allReports = MutableLiveData<List<DamageReport>>()
    val allReports: LiveData<List<DamageReport>> = _allReports

    private val _reportSaved = MutableLiveData<Boolean>()
    val reportSaved: LiveData<Boolean> = _reportSaved

    init {
        loadData()
    }

    fun loadData() {
        val context = getApplication<Application>()

        // Load roads
        val roadsJson = JsonDataManager.readJsonArrayFromAssets(context, "roads.json")
        val roadsList = mutableListOf<Road>()
        for (i in 0 until roadsJson.length()) {
            val obj = roadsJson.getJSONObject(i)
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
                    healthScore = obj.getInt("healthScore"),
                    totalReports = obj.getInt("totalReports"),
                    description = obj.getString("description"),
                    latitude = obj.getDouble("latitude"),
                    longitude = obj.getDouble("longitude")
                )
            )
        }
        _roads.value = roadsList

        // Load all reports
        loadReports()
    }

    private fun loadReports() {
        val context = getApplication<Application>()
        val reportsJson = JsonDataManager.getAllDamageReports(context)
        val reportsList = mutableListOf<DamageReport>()
        for (i in 0 until reportsJson.length()) {
            val obj = reportsJson.getJSONObject(i)
            reportsList.add(
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
        _allReports.value = reportsList.sortedByDescending { it.timestamp }
    }

    fun submitReport(
        roadId: Int,
        roadName: String,
        description: String,
        severity: String,
        photoPath: String,
        latitude: Double,
        longitude: Double,
        reporterName: String
    ) {
        val context = getApplication<Application>()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val timestamp = sdf.format(Date())

        val report = JSONObject().apply {
            put("roadId", roadId)
            put("roadName", roadName)
            put("description", description)
            put("severity", severity)
            put("photoPath", photoPath)
            put("latitude", latitude)
            put("longitude", longitude)
            put("timestamp", timestamp)
            put("reporterName", reporterName)
            put("status", "Pending")
        }

        JsonDataManager.saveDamageReport(context, report)
        _reportSaved.value = true
        loadReports()
    }

    fun getRoadNames(): List<String> {
        return _roads.value?.map { it.name } ?: emptyList()
    }

    fun getRoadByName(name: String): Road? {
        return _roads.value?.find { it.name == name }
    }
}
