package com.example.findmygolda.alerts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.findmygolda.database.AlertDatabaseDAO
import com.example.findmygolda.database.AlertEntity
import kotlinx.coroutines.*

class AlertsViewModel(
    val database: AlertDatabaseDAO,
    application: Application
) : AndroidViewModel(application)  {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val alerts = database.getAllAlerts()

   /* init {
        uiScope.launch{
            withContext(Dispatchers.IO){
                if (database.getAllAlerts().value.isNullOrEmpty()) {
                // saving to the room
                    database.insert(AlertEntity(title = "Golda Givat Shmuel", description = "2+2 on all ice creams"))
                    val lastAlert = database.getLastAlert()
                }
            }
        }

    }*/

    private suspend fun getAlertFromDatabase(): AlertEntity? {
        return withContext(Dispatchers.IO) {
            var alert = database.getLastAlert()
            if (alert?.id != alert?.id) {
                alert = null
            }
            alert
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}