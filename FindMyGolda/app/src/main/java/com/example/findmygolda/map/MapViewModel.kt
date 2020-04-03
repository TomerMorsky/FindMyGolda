package com.example.findmygolda.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.findmygolda.network.BranchApi
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

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main )

    init {
        getGoldaBranches()
    }

    private fun getGoldaBranches() {
        coroutineScope.launch {
            var getPropertiesDeferred =
                BranchApi.retrofitService.getProperties()
            try {
                var listResult = getPropertiesDeferred.await()
                _response.value =
                    "Success: ${listResult.size} Mars properties retrieved"
                _branches.value = listResult
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}