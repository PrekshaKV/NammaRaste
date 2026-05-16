package com.nammaraste.models

data class DamageReport(
    val id: Int,
    val roadId: Int,
    val roadName: String,
    val description: String,
    val severity: String,
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val reporterName: String,
    val status: String = "Pending"
)
