package com.example.findmygolda.network

import android.location.Location
import android.util.Log
import androidx.lifecycle.Observer
import com.example.findmygolda.map.MapFragment
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager() {
    private var branches = listOf<BranchProperty>()
        get() = branches
    private var map = MapFragment()


    init {
        map.onTitleChanged= { oldValue, newValue ->
            Log.i("change", "in locartion change")
        }
    }

    fun isBranchIn500(location: Location, branch: BranchProperty): Boolean{
        val branchLocation = Location("branch")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        val result = location!!.distanceTo(branchLocation) <= 500
        return result
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


