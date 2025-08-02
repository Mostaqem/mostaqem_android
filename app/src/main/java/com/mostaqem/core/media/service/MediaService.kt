package com.mostaqem.core.media.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioManagerCompat.requestAudioFocus
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
import com.google.common.collect.ImmutableList
import com.mostaqem.MainActivity
import com.mostaqem.R
import com.mostaqem.features.offline.domain.DownloadUtil

@UnstableApi
class MediaService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wasPlayingBeforeAudioLoss = false
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        val player = mediaSession?.player ?: return@OnAudioFocusChangeListener
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (player.isPlaying) {
                    wasPlayingBeforeAudioLoss = true
                    player.pause()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (player.isPlaying) {
                    wasPlayingBeforeAudioLoss = true
                    player.pause()
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player.volume = 0.2f
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                player.volume = 1.0f
                if (wasPlayingBeforeAudioLoss) {
                    player.play()
                    wasPlayingBeforeAudioLoss = false
                }
            }
        }
    }

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


        val player = ExoPlayer.Builder(this).setMediaSourceFactory(
            DefaultMediaSourceFactory(this).setDataSourceFactory(cacheDataSourceFactory)
        ).build()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY && player.playWhenReady) {
                    requestAudioFocus()
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                if (playWhenReady) {
                    requestAudioFocus()
                }
            }
        })

        mediaSession =
            MediaSession.Builder(this, player).setSessionActivity(pendingIntent).build()
        setMediaNotificationProvider(CustomNotificationProvider(this))

        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        abandonAudioFocus()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (player.playWhenReady) {
            player.pause()
        }
        abandonAudioFocus()
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    private fun requestAudioFocus(): Boolean {
        return audioFocusRequest?.let { request ->
            val result = audioManager.requestAudioFocus(request)
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } ?: false
    }

    private fun abandonAudioFocus() {
        audioFocusRequest?.let { request ->
            audioManager.abandonAudioFocusRequest(request)
        }
    }
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
        return super.addNotificationActions(
            mediaSession,

            notificationsButtons,
            builder,
            actionFactory,
        )
    }
}