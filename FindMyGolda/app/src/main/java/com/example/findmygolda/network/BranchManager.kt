package com.example.findmygolda.network

import android.location.Location
import android.util.Log
import androidx.lifecycle.Observer
import com.example.findmygolda.map.MapFragment
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager() {
     var branches = listOf<BranchProperty>()
//        get() = branches
//    private var map = MapFragment()


    init {
//        map.onTitleChanged= { oldValue, newValue ->
//            Log.i("change", "in locartion change")
//        }
    }

    fun isDistanceLessThen500Meters(location: Location, branch: BranchProperty): Boolean{
        val branchLocation = Location("")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        return (location!!.distanceTo(branchLocation) <= 500)
    }

    suspend fun getGoldaBranches(): List<BranchProperty>? {
        return withContext(Dispatchers.IO) {
            var listResult = listOf<BranchProperty>()
            val getBranchesDeferred = BranchApi.retrofitService.getProperties()
            try {
                listResult = getBranchesDeferred.await()
                branches = listResult
            } catch (e: Exception) { }
            listResult
        }
    }

}


