package com.mostaqem.core.media.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionCommand
import com.google.common.collect.ImmutableList
import com.mostaqem.MainActivity
import com.mostaqem.R
import com.mostaqem.features.offline.domain.DownloadUtil

@UnstableApi
class MediaService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var audioManager: AudioManager


    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()


        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE

        )

        val downloadCache = DownloadUtil.getDownloadCache(this)
        val dataSourceFactor = DefaultDataSource.Factory(this)

        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setCache(downloadCache)
                .setUpstreamDataSourceFactory(dataSourceFactor)
                .setCacheWriteDataSinkFactory(null)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(USAGE_MEDIA)
            .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)

            .setAudioAttributes(audioAttributes, true)

            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this)
                    .setDataSourceFactory(cacheDataSourceFactory)
            ).build()

        mediaSession =
            MediaSession.Builder(this, player)

                .setSessionActivity(pendingIntent).build()

        setMediaNotificationProvider(CustomNotificationProvider(this))

    }


    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (player.playWhenReady) {
            player.pause()
        }
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

}

@OptIn(UnstableApi::class)
class CustomNotificationProvider(context: Context) : DefaultMediaNotificationProvider(context) {
    override fun addNotificationActions(
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification.ActionFactory
    ): IntArray {
        val defaultPlayPauseCommandButton: CommandButton? = mediaButtons.getOrNull(1)
        val nextButton: CommandButton = CommandButton.Builder(CommandButton.ICON_NEXT)
            .setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT).build()
        val previousButton: CommandButton = CommandButton.Builder(CommandButton.ICON_PREVIOUS)
            .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS).build()

        val notificationsButtons = if (defaultPlayPauseCommandButton != null) {
            ImmutableList.builder<CommandButton>().apply {
                add(previousButton)
                add(defaultPlayPauseCommandButton)
                add(nextButton)
            }.build().also {
                setSmallIcon(R.drawable.logo)

            }
        } else {
            mediaButtons
        }
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return super.addNotificationActions(
            mediaSession,
            notificationsButtons,
            builder,
            actionFactory,
        )
    }
}
