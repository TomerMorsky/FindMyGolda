package com.example.findmygolda.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface AlertDatabaseDAO {
    @Insert
    fun insert(alert: AlertEntity)

    @Query("SELECT * FROM alerts ORDER BY id DESC")
    fun getAllAlerts() : LiveData<List<AlertEntity>>

    @Query("SELECT * FROM alerts ORDER BY id DESC LIMIT 1")
    fun getLastAlert(): AlertEntity?

    @Query("SELECT * from alerts WHERE branchId = :branchId ORDER BY time DESC LIMIT 1")
    fun getLastAlertOfBranch(branchId: Int): AlertEntity?


}