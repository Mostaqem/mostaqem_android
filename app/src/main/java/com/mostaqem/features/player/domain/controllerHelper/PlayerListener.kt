package com.mostaqem.features.player.domain.controllerHelper

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException

interface PlayerListenerCallbacks {
    fun onIsPlayingChanged(isPlaying: Boolean)
    fun onPlayerError(error: PlaybackException)
    fun onMediaItemTransition(mediaItem: MediaItem)
    fun onQueueNeeded(currentSurahId: Int, reciterId: Int)
}