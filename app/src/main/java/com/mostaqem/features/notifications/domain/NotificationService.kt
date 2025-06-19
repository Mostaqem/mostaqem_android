package com.mostaqem.features.notifications.domain

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import com.mostaqem.MainActivity
import com.mostaqem.R
import androidx.core.net.toUri

class NotificationService(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(defaultRecitationID: Int) {
        val alKahafChapterNumber = 18
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data =
                "https://mostaqemapp.online/quran/${alKahafChapterNumber}/${defaultRecitationID}".toUri()
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(context.getString(R.string.friday_await))
            .setContentText(context.getString(R.string.listen_friday))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, builder)
    }

    companion object {
        const val CHANNEL_ID = "friday"
    }
}