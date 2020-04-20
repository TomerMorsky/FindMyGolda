package com.example.findmygolda.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class BranchEntity constructor(
    @PrimaryKey
    val id: String,
    val address: String,
    val discounts: String,
    val latitude: Double,
    val longtitude: Double,
    val phone: String,
    val name: String)