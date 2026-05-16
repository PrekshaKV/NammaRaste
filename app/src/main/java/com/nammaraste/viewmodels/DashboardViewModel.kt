package com.nammaraste.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nammaraste.models.Road
import com.nammaraste.utils.JsonDataManager
import com.nammaraste.utils.HealthScoreCalculator

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _roads = MutableLiveData<List<Road>>()
    val roads: LiveData<List<Road>> = _roads

    private val _totalRoads = MutableLiveData<Int>()
    val totalRoads: LiveData<Int> = _totalRoads

    private val _averageHealth = MutableLiveData<Int>()
    val averageHealth: LiveData<Int> = _averageHealth

    private val _totalReports = MutableLiveData<Int>()
    val totalReports: LiveData<Int> = _totalReports

    private val _criticalRoads = MutableLiveData<Int>()
    val criticalRoads: LiveData<Int> = _criticalRoads

    private val _excellentCount = MutableLiveData<Int>()
    val excellentCount: LiveData<Int> = _excellentCount

    private val _goodCount = MutableLiveData<Int>()
    val goodCount: LiveData<Int> = _goodCount

    private val _fairCount = MutableLiveData<Int>()
    val fairCount: LiveData<Int> = _fairCount

    private val _poorCount = MutableLiveData<Int>()
    val poorCount: LiveData<Int> = _poorCount

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
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

        _roads.value = roadsList
        _totalRoads.value = roadsList.size

        val avgHealth = if (roadsList.isNotEmpty()) roadsList.map { it.healthScore }.average().toInt() else 0
        _averageHealth.value = avgHealth

        val allReports = JsonDataManager.getAllDamageReports(context)
        _totalReports.value = allReports.length()

        _criticalRoads.value = roadsList.count { it.healthScore < 40 }
        _excellentCount.value = roadsList.count { it.healthScore >= 80 }
        _goodCount.value = roadsList.count { it.healthScore in 60..79 }
        _fairCount.value = roadsList.count { it.healthScore in 40..59 }
        _poorCount.value = roadsList.count { it.healthScore < 40 }
    }
}
