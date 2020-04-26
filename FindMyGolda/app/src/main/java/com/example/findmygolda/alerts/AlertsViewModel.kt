package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.database.AlertDatabaseDAO
import kotlinx.coroutines.*

class AlertsViewModel(
    val database: AlertDatabaseDAO,
    application: Application
) : AndroidViewModel(application)  {

    private var viewModelJob = Job()
    val alerts = database.getAllAlerts()


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}