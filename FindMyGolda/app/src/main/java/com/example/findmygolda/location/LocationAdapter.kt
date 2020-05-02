package com.example.findmygolda.location

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mapbox.android.core.location.*

class LocationAdapter(val application: Application,
                      val listenToLocationChange:ILocationChanged
                  ) {
    private lateinit var locationEngine: LocationEngine
    private val callback = LocationChangeListen()

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?>
        get() = _currentLocation

    init {
        initLocationEngine()
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(application)
        val request = LocationEngineRequest
            .Builder(1000)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(5000)
            .build()
        locationEngine.requestLocationUpdates(request, callback, Looper.myLooper())
        locationEngine.getLastLocation(callback)
    }


    private inner class LocationChangeListen :
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation ?: return

            if (result.lastLocation != null) {
                val newLocation = Location(result.lastLocation)
                listenToLocationChange.LocationChanged(newLocation)
                _currentLocation.value = newLocation
//                mapboxMap.locationComponent.forceLocationUpdate(newLocation)
//                viewModel.checkBranchDistance(newLocation)
//                location = newLocation
            }
        }

        override fun onFailure(exception: Exception) {}
    }

}