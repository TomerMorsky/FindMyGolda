package com.example.findmygolda

import androidx.lifecycle.LiveData
import com.example.findmygolda.database.AlertDatabase
import com.example.findmygolda.database.BranchEntity
import com.example.findmygolda.network.BranchApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BranchesRepository(private val database: AlertDatabase) {
    val branches: LiveData<List<BranchEntity>> = database.branchDatabaseDAO.getBranches()

    suspend fun refreshBranches() {
        withContext(Dispatchers.IO) {
            val getBranchesDeferred = BranchApi.retrofitService.getProperties()
            val allBranches = getBranchesDeferred.await()
            database.branchDatabaseDAO.insert(allBranches)
        }
    }
}