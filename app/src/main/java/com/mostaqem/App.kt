package com.mostaqem

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mostaqem.features.history.domain.worker.DeleteWorker
import com.mostaqem.features.update.domain.UpdateWorker

import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        enqueueDeleteOldItemsWorker(this)
//        scheduleUpdateCheck(this)
    }

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder().setWorkerFactory(hiltWorkerFactory).build()
        }

    private fun enqueueDeleteOldItemsWorker(context: Context) {
        val deleteOldItemsRequest =
            PeriodicWorkRequestBuilder<DeleteWorker>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DeleteOldItemsWork", ExistingPeriodicWorkPolicy.KEEP, deleteOldItemsRequest
        )
    }

    fun scheduleUpdateCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<UpdateWorker>(
            12, TimeUnit.HOURS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "UpdateWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

}

