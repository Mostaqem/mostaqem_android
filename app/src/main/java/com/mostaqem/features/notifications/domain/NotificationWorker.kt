package com.mostaqem.features.notifications.domain

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mostaqem.dataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@HiltWorker
class FridayWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,

    ) : CoroutineWorker(context, workerParams) {
    val service = NotificationService(context)
    override suspend fun doWork(): Result {
        val showNotificationsEnabled = applicationContext.dataStore.data
            .map { it.fridayNotificationEnabled }
            .first()
        if (!showNotificationsEnabled) {
            return Result.success()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.failure()
            }
        }
        val recitationId =
            context.dataStore.data.map { it.recitationID }.first()
        service.showNotification(recitationId)
        return Result.success()
    }
}


