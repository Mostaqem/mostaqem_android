package com.mostaqem.features.player.domain.controllerHelper

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mostaqem.features.language.domain.LanguageManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerHelper @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val languageManager: LanguageManager
) {
    private var mediaController: MediaController? = null
    private var listenerCallbacks: PlayerListenerCallbacks? = null


    fun initController(callbacks: PlayerListenerCallbacks) {
        this.listenerCallbacks = callbacks
        mediaControllerFuture.addListener({
            mediaController = mediaControllerFuture.get().apply {
                addListener(createPlayerListener())
            }
        }, MoreExecutors.directExecutor())
    }

    private fun createPlayerListener() = object : Player.Listener{
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            listenerCallbacks?.onIsPlayingChanged(isPlaying)
            super.onIsPlayingChanged(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            listenerCallbacks?.onPlayerError(error)
            super.onPlayerError(error)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let { listenerCallbacks?.onMediaItemTransition(it) }
            super.onMediaItemTransition(mediaItem, reason)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                mediaController?.let { controller ->
                    if (controller.currentMediaItemIndex == controller.mediaItemCount - 1) {
                        val mediaItem = controller.currentMediaItem
                        mediaItem?.mediaMetadata?.let { metadata ->
                            val surahId = mediaItem.mediaId.toIntOrNull() ?: return
                            val reciterId = metadata.albumTitle?.toString()?.toIntOrNull() ?: return
                            listenerCallbacks?.onQueueNeeded(surahId, reciterId)
                        }
                    }
                }
            }
            super.onPlaybackStateChanged(playbackState)
        }
    }


}