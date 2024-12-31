package com.mostaqem.core.media.service

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.Player.Command
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList

class MediaService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
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
        val nextButton: CommandButton = CommandButton.Builder(CommandButton.ICON_NEXT).setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT).build()
        val previousButton: CommandButton = CommandButton.Builder(CommandButton.ICON_PREVIOUS).setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS).build()

        val notificationsButtons = if (defaultPlayPauseCommandButton != null) {
            ImmutableList.builder<CommandButton>().apply {
                add(previousButton)
                add(defaultPlayPauseCommandButton)
                add(nextButton)
            }.build()
        } else {
            mediaButtons
        }
        return super.addNotificationActions(
            mediaSession,
            notificationsButtons,
            builder,
            actionFactory
        )
    }
}