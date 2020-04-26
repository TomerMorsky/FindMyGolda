package com.example.findmygolda.network

import android.location.Location
import android.util.Log
import androidx.lifecycle.Observer
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.map.MapFragment
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager() {
     var branches = listOf<BranchEntity>()
//        get() = branches
//    private var map = MapFragment()


    init {
//        map.onTitleChanged= { oldValue, newValue ->
//            Log.i("change", "in locartion change")
//        }
    }

    fun isDistanceInRange(location: Location, branch: BranchEntity, range:Int): Boolean{
        val branchLocation = Location("")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        return (location!!.distanceTo(branchLocation) <= range)
    }

    suspend fun getGoldaBranches(): List<BranchEntity>? {
        return withContext(Dispatchers.IO) {
            var listResult = listOf<BranchEntity>()
            val getBranchesDeferred = BranchApi.retrofitService.getProperties()
            try {
                listResult = getBranchesDeferred.await()
                branches = listResult
            } catch (e: Exception) { }
            listResult
        }
    }

}


