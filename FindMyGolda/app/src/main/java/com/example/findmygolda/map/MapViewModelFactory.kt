package com.example.findmygolda.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findmygolda.alerts.AlertManager

class MapViewModelFactory(
    private val application: Application,
    private var maxDistanceFromBranch: Int = 500,
    private var minTimeBetweenAlers: Long = MIN_TIME_BETWEEN_ALERTS,
    private var alertManager: AlertManager
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application, maxDistanceFromBranch, minTimeBetweenAlers,alertManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
