package com.example.findmygolda.location

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider

class LocationManager(val application: Application) : LocationEngineListener {
    var locationEngine: LocationEngine? = null
    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?>
        get() = _currentLocation

    var curLocation : Location? = null
        get() = curLocation

    init {
        initializeLocationEngine()

    }

    override fun onLocationChanged(location: Location?) {
        location?.run {
            _currentLocation.value = this
            curLocation = this
        }
    }

    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    @SuppressWarnings("MissingPermission")
    fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(application).obtainBestLocationEngineAvailable()// Get the location
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()
        locationEngine?.addLocationEngineListener(this)

        val lastLocation = locationEngine?.lastLocation
        if (lastLocation != null) {
            _currentLocation.value = lastLocation
        } else {
            locationEngine?.addLocationEngineListener(this)
        }
    }
}