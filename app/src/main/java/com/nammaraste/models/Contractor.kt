package com.nammaraste.models

data class Contractor(
    val id: Int,
    val name: String,
    val company: String,
    val phone: String,
    val email: String,
    val address: String,
    val roadsBuilt: Int,
    val rating: Float,
    val specialization: String
)
