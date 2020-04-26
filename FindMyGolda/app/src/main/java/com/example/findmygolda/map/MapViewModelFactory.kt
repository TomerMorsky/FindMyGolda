package com.example.findmygolda.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MapViewModelFactory(
    private val application: Application,
    private var maxDistanceFromBranch: Int = 500,
    private var minTimeBetweenAlers: Long = MIN_TIME_BETWEEN_ALERTS
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(application, maxDistanceFromBranch, minTimeBetweenAlers) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
