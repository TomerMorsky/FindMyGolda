package com.example.findmygolda.alerts

import android.app.Application
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.location.ILocationChanged
import com.example.findmygolda.network.BranchManager
import kotlinx.coroutines.*

class AlertManager(val application: Application, val branchManager: BranchManager):ILocationChanged {
    private val dataSource = (AlertDatabase.getInstance(application)).alertDatabaseDAO
    val alerts = dataSource.getAllAlerts()
    private var alertManagerJob = Job()
    private val coroutineScope = CoroutineScope(
        alertManagerJob + Dispatchers.Main )
    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val maxDistanceFromBranch = preferences.getInt("radiusFromBranch", 5).times(100)
    private val minTimeBetweenAlerts = parseMinutesToMilliseconds(preferences.getInt("timeBetweenNotifications", 1).times(5))


    override fun LocationChanged(location: Location) {
        // Every time location change this method called
        alertIfNeeded(location)
    }

    private fun alertIfNeeded(location: Location){
        val branches = branchManager.branches.value
        val dataSource = (AlertDatabase.getInstance(application)).alertDatabaseDAO
        branches?.forEach{
            if(branchManager.isDistanceInRange(location, it, maxDistanceFromBranch)){
                coroutineScope.launch{
                    withContext(Dispatchers.IO){
                        val lastAlert = dataSource.getLastAlertOfBranch(it.id.toInt())
                        if(hasTimePast(lastAlert)){
                            dataSource.insert(
                                AlertEntity(title = it.name,
                                    description = it.discounts,
                                    branchId = it.id.toInt())
                            )
                            NotificationHelper(application.applicationContext).createNotification(it.name, it.discounts)
                        }
                    }
                }
            }
        }

    }

    private fun hasTimePast(lastAlert : AlertEntity?): Boolean {
        if (lastAlert == null)
            return true
        return (System.currentTimeMillis() - lastAlert.time) >= minTimeBetweenAlerts
    }

    private fun parseMinutesToMilliseconds(minutes : Int) : Long{
        return (minutes * 60000).toLong()
    }
}