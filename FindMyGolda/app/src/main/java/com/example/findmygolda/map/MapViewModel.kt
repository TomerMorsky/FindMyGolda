package com.example.findmygolda.map

import android.app.Application
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.BranchesRepository
import com.example.findmygolda.alerts.NotificationHelper
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.AlertEntity
import com.example.findmygolda.network.BranchManager
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import kotlinx.coroutines.*
import java.lang.Exception

const val MIN_TIME_BETWEEN_ALERTS = 300000L
class MapViewModel(val application: Application,
                   var maxDistanceFromBranch: Int = 500,
                   var minTimeBetweenAlers: Long = MIN_TIME_BETWEEN_ALERTS) : ViewModel(), LocationEngineListener {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _focusOnUserLocation = MutableLiveData<Boolean?>()
    val focusOnUserLocation: LiveData<Boolean?>
        get() = _focusOnUserLocation

    private val _navigateToAlertsFragment = MutableLiveData<Boolean?>()
    val navigateToAlertsFragment: LiveData<Boolean?>
        get() = _navigateToAlertsFragment

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main )

    private val branchManager = BranchManager()

    private val branchRepository = BranchesRepository(AlertDatabase.getInstance(application))
    val branches = branchRepository.branches

    var locationEngine: LocationEngine? = null

    var currentLocation : Location? = null

    init {
        getGoldaBranches()

    }

    fun getGoldaBranches() {
        coroutineScope.launch {
            try {
                branchRepository.refreshBranches()
                initializeLocationEngine()
            } catch (e: Exception) {
                // Probably no internet connection
            }

        }
    }

    fun alertIfNeeded(location: Location){
        val branches = branches.value
        val dataSource = (AlertDatabase.getInstance(application)).alertDatabaseDAO
        if (branches != null) {
            for(branch in branches){
                if(branchManager.isDistanceInRange(location, branch, maxDistanceFromBranch)){
                    coroutineScope.launch{
                        withContext(Dispatchers.IO){
                            // saving to the room
                            val lastAlert = dataSource.getLastAlertOfBranch(branch.id.toInt())
                            if(hasTimePast(lastAlert)){
                                dataSource.insert(AlertEntity(title = branch.name,
                                    description = branch.discounts,
                                    branchId = branch.id.toInt()))
                                NotificationHelper(application.applicationContext).createNotification(branch.name, branch.discounts)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hasTimePast(lastAlert : AlertEntity?): Boolean {
        if (lastAlert == null)
            return true
        return (System.currentTimeMillis() - lastAlert.time) >= minTimeBetweenAlers
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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

    override fun onLocationChanged(location: Location?) {
        location?.run {
            alertIfNeeded(this)
            currentLocation = this
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
            currentLocation = lastLocation
        } else {
            locationEngine?.addLocationEngineListener(this)
        }
    }

    fun currentLocation():Location?{
        return currentLocation
    }

}