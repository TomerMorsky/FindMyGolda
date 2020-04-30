package com.example.findmygolda.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.findmygolda.BranchesRepository
import com.example.findmygolda.database.AlertDatabase
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = AlertDatabase.getInstance(applicationContext)
        val repository = BranchesRepository(database)
        try {
            repository.refreshBranches( )
            //Timber.d("Work request for sync is run")
        } catch (e: HttpException) {
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "com.example.android.FindMyGolda.RefreshDataWorker"
    }
}