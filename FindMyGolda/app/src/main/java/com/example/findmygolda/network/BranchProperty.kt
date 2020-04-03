package com.example.findmygolda.network

import com.squareup.moshi.Json

data class BranchProperty(
    val address: String,
    val discounts: String,
    val id: String,
    val latitude: Double,
    val longtitude: Double,
    val phone: String,
    val name: String)