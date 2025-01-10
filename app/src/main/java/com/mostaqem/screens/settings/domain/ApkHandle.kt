package com.mostaqem.screens.settings.domain

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class ApkHandle(private val context: Context) {
    fun installApk(context: Context, apkFile: File) {
        val isAllowed = context.packageManager.canRequestPackageInstalls()
        if (!isAllowed) {
            notifyUserToInstallManually(context,apkFile)
            return
        }


        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)

    }

    suspend fun downloadApk(url: String, context: Context): File {
        val file = File(context.getExternalFilesDir(null), "update.apk")
        val response = withContext(Dispatchers.IO) {
            OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        }
        if (response.isSuccessful) {
            response.body?.byteStream()?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }

    fun notifyUserToInstallManually(context: Context, apkFile: File) {
        val channelId = "install_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android 8.0+)

            val channel = NotificationChannel(
                channelId,
                "Install Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Install Update")
            .setContentText("Tap to install the new version")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

}