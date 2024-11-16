package com.mostaqem.screens.player.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mostaqem.R
import com.mostaqem.screens.player.data.PlayerSurah
import com.mostaqem.screens.surahs.data.Data
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val repository: SurahRepository,

    ) : ViewModel() {
    var playerState = mutableStateOf(PlayerSurah())

    private var mediaController: MediaController? = null

    private val _playPauseIcon = MutableStateFlow(R.drawable.outline_pause_24)
    val playPauseIcon: StateFlow<Int> = _playPauseIcon

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _positionPercentage = MutableStateFlow<Float>(0f)
    val positionPercentage: StateFlow<Float> = _positionPercentage

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    init {
        mediaControllerFuture.addListener(
            {
                mediaController = mediaControllerFuture.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            _playPauseIcon.value = R.drawable.outline_pause_24
                        } else {
                            _playPauseIcon.value = R.drawable.outline_play_arrow_24
                        }
                    }

                })
            },
            MoreExecutors.directExecutor()
        )

        viewModelScope.launch {
            while (true) {
                val duration = mediaController?.duration ?: 0L
                _duration.value = duration
                val position = mediaController?.currentPosition ?: 0L
                _currentPosition.value = position
                _positionPercentage.value = if (duration > 0) {
                    (position / duration.toFloat()) * 1f
                } else 0f

                delay(1000)
            }
        }

    }

    private fun playerSetItem(url: String) {
        val metadata: MediaMetadata = MediaMetadata.Builder()
            .setTitle(playerState.value.surah?.arabicName)
            .setArtist(playerState.value.reciter.arabicName)
            .setArtworkUri(playerState.value.surah?.image?.toUri())
            .build()
        val mediaItem: MediaItem =
            MediaItem.Builder().setUri(url).setMediaMetadata(metadata).build()
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    fun fetchMediaUrl() {
        val surahID: Int = playerState.value.surah?.id ?: 1
        val reciterID: Int = playerState.value.reciter.id
        viewModelScope.launch() {
            val url: String = repository.getSurahUrl(
                surahID = surahID, reciterID = reciterID, recitationID = null
            ).response.url
            playerSetItem(url)


        }
        mediaController?.play()
    }

    fun handlePlayPause() {
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
        } else {
            mediaController?.play()
        }
    }

    fun addMediaItem(mediaItem: MediaItem){
        mediaController?.addMediaItem(mediaItem)
    }

    fun seekToPosition(position: Float) {
        _positionPercentage.value = position
        val scaledPosition = (position * (mediaController?.duration ?: 0L) / 1f).toLong()
        mediaController?.seekTo(scaledPosition)
    }

    fun seekNext() {
        val currentSurahID: Int = playerState.value.surah?.id ?: 1
        if (currentSurahID <= 114) {
            val nextSurahID: Int = currentSurahID + 1
            val reciterID: Int = playerState.value.reciter.id
            viewModelScope.launch() {
                val response = repository.getSurahUrl(
                    surahID = nextSurahID, reciterID = reciterID, recitationID = null
                ).response
                playerState.value =
                    playerState.value.copy(surah = response.surah, reciter = response.recitation.reciter)
                playerSetItem(response.url)
                mediaController?.play()

            }

        }

    }

    fun seekPrevious() {
        val currentSurahID: Int = playerState.value.surah?.id ?: 1
        Log.d("CurrentID", "seekPrevious: $currentSurahID")
        if (currentSurahID > 1) {
            val previousSurahID: Int = currentSurahID - 1
            val reciterID: Int = playerState.value.reciter.id
            viewModelScope.launch() {
                val response: Data = repository.getSurahUrl(
                    surahID = previousSurahID, reciterID = reciterID, recitationID = null
                ).response
                playerState.value =
                    playerState.value.copy(surah = response.surah, reciter = response.recitation.reciter)
                playerSetItem(response.url)
                mediaController?.play()

            }
            return
        }
        mediaController?.seekTo(0)

    }

}

@SuppressLint("DefaultLocale")
fun Number.toHoursMinutesSeconds(): String {
    val millis = this.toLong()
    val hours = (millis / 1000) / 3600
    val minutes = ((millis / 1000) % 3600) / 60
    val seconds = (millis / 1000) % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}



