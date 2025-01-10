package com.mostaqem.screens.player.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mostaqem.R
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.dataStore
import com.mostaqem.screens.player.data.BottomSheetType
import com.mostaqem.screens.player.data.PlayerSurah
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Data
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val repository: SurahRepository,
    private val chillPlayer: ExoPlayer
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Player Error", "${exception.message}")
    }
    var playerState = mutableStateOf(PlayerSurah())

    private var mediaController: MediaController? = null

    var queuePlaylist = mutableListOf<MediaItem>()

    private val _playPauseIcon = MutableStateFlow(R.drawable.outline_pause_24)
    val playPauseIcon: StateFlow<Int> = _playPauseIcon

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _positionPercentage = MutableStateFlow<Float>(0f)
    val positionPercentage: StateFlow<Float> = _positionPercentage

    private val _currentBottomSheet = MutableStateFlow<BottomSheetType>(BottomSheetType.None)
    val currentBottomSheet: StateFlow<BottomSheetType> = _currentBottomSheet

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    init {
        mediaControllerFuture.addListener(
            {
                mediaController = mediaControllerFuture.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            _playPauseIcon.update { R.drawable.outline_pause_24 }
                            chillPlayer.play()
                        } else {
                            _playPauseIcon.update { R.drawable.outline_play_arrow_24 }
                            chillPlayer.pause()
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        playerState.value = playerState.value.copy(
                            surah = Surah(
                                id = mediaItem?.mediaId!!.toInt(),
                                image = mediaItem.mediaMetadata.artworkUri.toString(),
                                arabicName = mediaItem.mediaMetadata.title.toString(),
                                complexName = "",
                                versusCount = 0,
                                revelationPlace = ""
                            ),
                            reciter = Reciter(
                                id = mediaItem.mediaMetadata.albumTitle.toString().toInt(),
                                arabicName = mediaItem.mediaMetadata.artist.toString(),
                                image = mediaItem.mediaMetadata.albumArtist.toString(),
                                englishName = ""
                            ),
                            recitationID = mediaItem.mediaMetadata.genre.toString().toInt()
                        )
                        super.onMediaItemTransition(mediaItem, reason)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            val currentIndex = mediaController?.currentMediaItemIndex
                            val queueSize = mediaController?.mediaItemCount ?: 0
                            if (currentIndex == queueSize - 1) {
                                val currentChapterId = playerState.value.surah?.id ?: 1
                                val reciterID = playerState.value.reciter.id
                                getQueueUrls(currentChapterId, reciterID)
                            }
                        }
                        super.onPlaybackStateChanged(playbackState)
                    }
                })
            }, MoreExecutors.directExecutor()
        )

        viewModelScope.launch {
            while (true) {
                val rawDuration = mediaController?.duration ?: 0L
                val duration = if (rawDuration < 0) 0L else rawDuration
                _duration.value = duration
                val position = mediaController?.currentPosition ?: 0L
                _currentPosition.value = position
                _positionPercentage.value = if (duration > 0) {
                    (position / duration.toFloat()) * 1f
                } else 0f
                delay(1000L)
            }
        }
    }

    private fun setMetadata(data: Data): MediaItem {
        val metadata: MediaMetadata =
            MediaMetadata.Builder()
                .setTitle(data.surah.arabicName)
                .setGenre(data.recitation.id.toString())
                .setAlbumTitle(data.recitation.reciter.id.toString())
                .setAlbumArtist(data.recitation.reciter.image)
                .setArtist(data.recitation.reciter.arabicName)
                .setArtworkUri(data.surah.image.toUri()).build()
        return MediaItem.Builder().setUri(data.url).setMediaMetadata(metadata)
            .setMediaId(data.surah.id.toString()).build()

    }

    private fun playerSetItem(data: Data) {
        val mediaItem: MediaItem = setMetadata(data)
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    fun fetchMediaUrl() {
        val surahID: Int = playerState.value.surah!!.id
        val reciterID: Int = playerState.value.reciter.id
        val recitationID: Int? = playerState.value.recitationID
        viewModelScope.launch(errorHandler) {
            val data: Data = repository.getSurahUrl(
                surahID = surahID, reciterID = reciterID, recitationID = recitationID
            ).response
            playerSetItem(data)
            getQueueUrls(surahID, reciterID)
        }
        mediaController?.play()
    }

    private fun getQueueUrls(currentSurahID: Int, reciterID: Int) {
        val previousChapters = (1..3).map { currentSurahID - it }.filter { it > 0 } // Ensure IDs are valid
        val nextChapters = (1..5).map { currentSurahID + it }

        val previousMediaItems = mutableSetOf<MediaItem>()

        val nextMediaItems = mutableSetOf<MediaItem>()
        val previousJobs = previousChapters.map { id ->
            viewModelScope.async(errorHandler) {
                val data: Data = repository.getSurahUrl(
                    surahID = id, reciterID = reciterID, recitationID = null
                ).response
                val mediaItem = setMetadata(data)
                previousMediaItems.add(mediaItem)
                mediaController?.addMediaItems(0, previousMediaItems.reversed())
            }
        }

        val currentMediaItem = viewModelScope.async(errorHandler) {
            val data: Data = repository.getSurahUrl(
                surahID = currentSurahID, reciterID = reciterID, recitationID = null
            ).response
            setMetadata(data)
        }

        val nextJobs = nextChapters.map { id ->
            viewModelScope.async(errorHandler) {
                val data: Data = repository.getSurahUrl(
                    surahID = id, reciterID = reciterID, recitationID = null
                ).response
                val mediaItem = setMetadata(data)
                nextMediaItems.add(mediaItem)
                mediaController?.addMediaItems(nextMediaItems.toMutableList())
            }
        }
        viewModelScope.launch {
            previousJobs.awaitAll()
            nextJobs.awaitAll()
            val currentItem = currentMediaItem.await()
            queuePlaylist = (previousMediaItems.reversed() + currentItem + nextMediaItems).toMutableList()
            Log.d("QUEUEPLAYLIST", "Queue: $queuePlaylist")
        }

    }


    fun handlePlayPause() {
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
            chillPlayer.pause()
        } else {
            mediaController?.play()
            chillPlayer.play()

        }

    }

    fun seekToPosition(position: Float) {
        _positionPercentage.update { position }
        val scaledPosition = (position * (mediaController?.duration ?: 0L) / 1f).toLong()
        mediaController?.seekTo(scaledPosition)
    }

    fun seekNext() {
        mediaController?.seekToNext()
    }

    fun seekPrevious() {
        val currentSurahID: Int = playerState.value.surah?.id ?: 1
        if (currentSurahID > 1) {
            mediaController?.seekToPrevious()
            return
        }
        mediaController?.seekTo(0)
    }

    fun addNext(surahID: Int) {

        val currentMediaItemIndex: Int? = mediaController?.currentMediaItemIndex
        val reciterID: Int = playerState.value.reciter.id
        if (currentMediaItemIndex != null) {
            viewModelScope.launch {
                val data: Data = repository.getSurahUrl(
                    surahID = surahID, reciterID = reciterID, recitationID = null
                ).response
                val mediaItem = setMetadata(data)
                mediaController?.addMediaItem(currentMediaItemIndex + 1, mediaItem)
                queuePlaylist.add(currentMediaItemIndex, mediaItem)
                SnackbarController.sendEvent(
                    events = SnackbarEvents(
                        message = "تم اضافة السورة في التالي"
                    )
                )
            }

        }

    }

    fun addMediaItem(surahID: Int) {
        val reciterID: Int = playerState.value.reciter.id
        viewModelScope.launch {
            val data: Data = repository.getSurahUrl(
                surahID = surahID, reciterID = reciterID, recitationID = null
            ).response
            val mediaItem = setMetadata(data)
            mediaController?.addMediaItem(mediaItem)
            queuePlaylist.add(mediaItem)
            SnackbarController.sendEvent(
                events = SnackbarEvents(
                    message = "تم اضافة السورة في قائمة التشغيل"
                )
            )

        }

    }

    fun showBottomSheet(sheetType: BottomSheetType) {
        _currentBottomSheet.update { sheetType }
    }

    fun hideBottomSheet() {
        _currentBottomSheet.update { BottomSheetType.None }
    }

    fun changeShape(id: String, context: Context) {

        viewModelScope.launch {
            context.dataStore.updateData {
                it.copy(shapeID = id)
            }
        }

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



