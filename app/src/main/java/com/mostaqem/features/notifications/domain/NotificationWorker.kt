package com.mostaqem.features.notifications.domain

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mostaqem.R
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
        val recitation =
            context.dataStore.data.map { it.recitation }.first()
        service.showReminderNotification(
            defaultRecitationID = recitation.id,
            details = context.getString(R.string.listen_friday),
            chapterNumber = 18,
            heading = context.getString(R.string.friday_await)
        )
        return Result.success()
    }
}


@HiltWorker
class NightsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,

    ) : CoroutineWorker(context, workerParams) {
    val service = NotificationService(context)
    override suspend fun doWork(): Result {
        val showNotificationsEnabled = applicationContext.dataStore.data
            .map { it.almulkNotificationEnabled }
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
        val recitation =
            context.dataStore.data.map { it.recitation }.first()
        service.showReminderNotification(
            defaultRecitationID = recitation.id,
            details = context.getString(R.string.nights_await),
            chapterNumber = 67,
            heading = context.getString(R.string.listen_almulk)
        )
        return Result.success()
    }
}



