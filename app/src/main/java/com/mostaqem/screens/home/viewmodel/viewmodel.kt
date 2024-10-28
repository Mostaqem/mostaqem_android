package com.mostaqem.screens.home.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
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

    private val mediaController = mutableStateOf<MediaController?>(null)

    val playerIcon = mutableIntStateOf(R.drawable.outline_play_arrow_24)


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
                mediaController.value = mediaControllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )

        mediaController.value?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playerIcon.intValue = R.drawable.outline_pause_24
                } else {
                    playerIcon.intValue = R.drawable.outline_play_arrow_24
                }
            }

        })
    }


    private fun playerSetItem(url: String) {
        mediaController.value?.setMediaItem(MediaItem.fromUri(url))
        mediaController.value?.prepare()
    }

    fun fetchMediaUrl() {
        viewModelScope.launch(errorHandler) {
            val surahID: Int = playerState.value.surah?.id ?: 0
            val reciterID: Int = playerState.value.reciter.id

            val url: String = repository.getSurahUrl(
                surahID = surahID, reciterID = reciterID, recitationID = null
            ).response.url
            playerSetItem(url)

        }
    }

    fun handlePlayPause() {
        if (mediaController.value?.isPlaying == true) {
            mediaController.value?.pause()
        } else {
            mediaController.value?.play()
        }
    }


}





