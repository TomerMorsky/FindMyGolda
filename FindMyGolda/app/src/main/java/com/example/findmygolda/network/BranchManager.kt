package com.example.findmygolda.network

import android.app.Application
import android.location.Location
import com.example.findmygolda.BranchesRepository
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.BranchEntity
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager(val application: Application) {
//    val dataSource = (AlertDatabase.getInstance(application)).branchDatabaseDAO
//    var branches = dataSource.getBranches()
    private val branchRepository = BranchesRepository(AlertDatabase.getInstance(application))
    val branches = branchRepository.branches
    private var branchManagerJob = Job()
    private val coroutineScope = CoroutineScope(
        branchManagerJob + Dispatchers.Main )

    init {
//        map.onTitleChanged= { oldValue, newValue ->
//            Log.i("change", "in locartion change")
//        }
        getGoldaBranches()
    }

    fun isDistanceInRange(location: Location, branch: BranchEntity, range:Int): Boolean{
        val branchLocation = Location("")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        return (location!!.distanceTo(branchLocation) <= range)
    }

//    suspend fun getGoldaBranches(): List<BranchEntity>? {
//        return withContext(Dispatchers.IO) {
//            var listResult = listOf<BranchEntity>()
//            val getBranchesDeferred = BranchApi.retrofitService.getProperties()
//            try {
//                listResult = getBranchesDeferred.await()
//            } catch (e: Exception) { }
//            listResult
//        }
//    }

    private fun getGoldaBranches() {
        coroutineScope.launch {
            try {
                branchRepository.refreshBranches()
            } catch (e: Exception) {
                // Probably no internet connection
            }

        }
    }

}


