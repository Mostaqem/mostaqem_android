package com.mostaqem.screens.home.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mostaqem.R
import com.mostaqem.screens.home.data.ReciterState
import com.mostaqem.screens.home.data.SurahState
import com.mostaqem.screens.home.data.player.PlayerSurah
import com.mostaqem.screens.home.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val mediaControllerFuture: ListenableFuture<MediaController>
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Surah Error", "${exception.message}")
        surahState.value = surahState.value.copy(isLoading = false, isError = exception.message)
        reciterState.value = reciterState.value.copy(isLoading = false, isError = exception.message)
    }

    val surahState = mutableStateOf<SurahState>(SurahState(surahs = emptyList(), isLoading = true))

    val reciterState =
        mutableStateOf<ReciterState>(ReciterState(reciters = emptyList(), isLoading = true))

    var playerState = mutableStateOf(PlayerSurah())

    val isPlayerVisible = mutableStateOf(false)

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

        viewModelScope.launch(errorHandler) {
            val surahs = repository.getRemoteSurahs()

            surahState.value =
                surahState.value.copy(surahs = surahs.response.surahData, isLoading = false)

        }
        viewModelScope.launch(errorHandler) {
            val reciters = repository.getRemoteReciters()
            reciterState.value =
                reciterState.value.copy(reciters = reciters.response.reciters, isLoading = false)

        }

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
                    (position / duration.toFloat()) * 100f
                } else 0f

                delay(1000)
            }
        }


    }


    private fun playerSetItem(url: String) {
        mediaController?.setMediaItem(MediaItem.fromUri(url))
        mediaController?.prepare()

    }

    fun fetchMediaUrl() {
        viewModelScope.launch(errorHandler) {
            val surahID: Int = playerState.value.surah?.id ?: 0
            val reciterID: Int = playerState.value.reciter.id

            val url: String = repository.getSurahUrl(
                surahID = surahID, reciterID = reciterID, recitationID = null
            ).response.url
            playerSetItem(url)
            mediaController?.play()


        }
    }

    fun handlePlayPause() {
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
        } else {
            mediaController?.play()
        }
    }

    fun seekToPosition(position: Float) {
        _positionPercentage.value = position
        val scaledPosition = (position * (mediaController?.duration ?: 0L) / 100f).toLong()
        mediaController?.seekTo(scaledPosition)

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





