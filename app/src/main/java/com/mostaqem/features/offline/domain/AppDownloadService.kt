package com.mostaqem.features.offline.domain

import android.app.Notification
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import com.mostaqem.R

@UnstableApi
class AppDownloadService : DownloadService(
    NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    CHANNEL_ID,
    R.string.downloads,
    R.string.downloads
) {

    override fun getDownloadManager(): DownloadManager {
        return DownloadUtil.getDownloadManager(this)

    }

    override fun getScheduler(): Scheduler? = null

    override fun getForegroundNotification(
        downloads: List<Download>,
        notMetRequirements: Int
    ): Notification {
        return DownloadNotificationHelper(this, CHANNEL_ID)
            .buildProgressNotification(
                this,
                R.drawable.logo,
                null,
                "Downloading...",
                downloads,
                notMetRequirements
            )
    }

    companion object {
        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "download_channel"

    }


}