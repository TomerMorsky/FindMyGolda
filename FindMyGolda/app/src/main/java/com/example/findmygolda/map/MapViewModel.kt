package com.example.findmygolda.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.network.BranchApi
import com.example.findmygolda.network.BranchManager
import com.example.findmygolda.network.BranchProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MapViewModel : ViewModel() {
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    private val _branches = MutableLiveData<List<BranchProperty>>()
    val branches: LiveData<List<BranchProperty>>
        get() = _branches

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

    init {
        getGoldaBranches()
    }

    fun getGoldaBranches() {
        coroutineScope.launch {
            _branches.value = branchManager.getGoldaBranches()
        }
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


}