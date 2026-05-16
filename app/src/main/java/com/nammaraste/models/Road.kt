package com.nammaraste.models

data class Road(
    val id: Int,
    val name: String,
    val location: String,
    val taluka: String,
    val lengthKm: Double,
    val contractorId: Int,
    val contractorName: String,
    val builtDate: String,
    val warrantyEndDate: String,
    val roadType: String,
    val healthScore: Int,
    val totalReports: Int,
    val description: String,
    val latitude: Double,
    val longitude: Double
)
