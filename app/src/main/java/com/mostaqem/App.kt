package com.mostaqem

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mostaqem.features.history.domain.worker.DeleteWorker
import com.mostaqem.features.notifications.domain.FridayWorker
import com.mostaqem.features.notifications.domain.NightsWorker
import com.mostaqem.features.notifications.domain.NotificationService
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        enqueueDeleteOldItemsWorker(this)
        createNotificationChannel()
        enqueueFridayNotification(this)
        enqueueNightsNotification(this)
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


    private fun enqueueFridayNotification(context: Context) {
        val initialDelay = calculateInitialDelay()
        val fridayRequest =
            PeriodicWorkRequestBuilder<FridayWorker>(7, TimeUnit.DAYS).setInitialDelay(
                initialDelay, TimeUnit.MILLISECONDS
            )
                .addTag("friday_notification_tag")
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "FridayNotificationWork", // Unique name for your work
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
            fridayRequest
        )
    }

    private fun enqueueNightsNotification(context: Context) {
        val initialDelay = calculateInitialDelay()
        val nightlyRequest =
            PeriodicWorkRequestBuilder<NightsWorker>(
                1, TimeUnit.DAYS // Changed from 7 days to 1 day for daily
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("nights_notification_tag")
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "NightsNotificationWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            nightlyRequest
        )
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.CHANNEL_ID,
            "channels",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Hello This is description"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun calculateNightsInitialDay(): Long {
        val now = Calendar.getInstance()
        val desiredNotificationTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (desiredNotificationTime.before(now)) {
            desiredNotificationTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        return desiredNotificationTime.timeInMillis - now.timeInMillis
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        var daysUntilFriday = 0
        daysUntilFriday = if (today <= Calendar.FRIDAY) {
            Calendar.FRIDAY - today
        } else {
            (Calendar.SATURDAY - today) + Calendar.FRIDAY
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If today is Friday and it's already past 9 AM, schedule for next Friday
        if (today == Calendar.FRIDAY && (currentHour > 9 || (currentHour == 9 && currentMinute > 0))) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val currentTimeMillis = System.currentTimeMillis()
        val targetTimeMillis = calendar.timeInMillis

        // If targetTimeMillis is in the past, add a week
        if (targetTimeMillis <= currentTimeMillis) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            return calendar.timeInMillis - currentTimeMillis
        }

        return targetTimeMillis - currentTimeMillis
    }




}

