package com.example.findmygolda.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val time: Long = System.currentTimeMillis(),
    val title: String,
    val description: String,
    val branchId : Int
)