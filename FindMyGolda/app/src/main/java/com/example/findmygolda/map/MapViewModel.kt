package com.example.findmygolda.map

import android.app.Application
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.example.findmygolda.BranchesRepository
import com.example.findmygolda.MainActivity
import com.example.findmygolda.R
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.alerts.NotificationHelper
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.location.LocationAdapter
import com.example.findmygolda.network.BranchManager
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.properties.Delegates

const val MIN_TIME_BETWEEN_ALERTS = 300000L
class MapViewModel(val application: Application,
                   var maxDistanceFromBranch: Int = 500,
                   var minTimeBetweenAlers: Long = MIN_TIME_BETWEEN_ALERTS,
                   val alertManager : AlertManager) : ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _focusOnUserLocation = MutableLiveData<Boolean?>()
    val focusOnUserLocation: LiveData<Boolean?>
        get() = _focusOnUserLocation

    private val _navigateToAlertsFragment = MutableLiveData<Boolean?>()
    val navigateToAlertsFragment: LiveData<Boolean?>
        get() = _navigateToAlertsFragment

    var  locationManager : LocationAdapter

    init {
        //getGoldaBranches()
        locationManager = LocationAdapter(application,alertManager)
        //currentLocation = locationManager.currentLocation
        //mainActivity = requireNotNull(application) as MainActivity


    }

    fun onAlertsButtonClicked(){
        _navigateToAlertsFragment.value = true
    }

    fun doneNavigateToAlertsFragment(){
        _navigateToAlertsFragment.value = false;
    }

    fun focusOnUserLocationClicked(){
        _focusOnUserLocation.value = true;
    }

    fun doneFocusOnUserLocation(){
        _focusOnUserLocation.value = false;
    }
}