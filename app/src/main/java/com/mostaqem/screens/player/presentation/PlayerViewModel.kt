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
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mostaqem.R
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.NetworkStatus
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.dataStore
import com.mostaqem.screens.player.data.BottomSheetType
import com.mostaqem.screens.player.data.PlayerSurah
import com.mostaqem.screens.player.domain.PlayerRepository
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.domain.ReciterRepository
import com.mostaqem.screens.surahs.data.AudioData
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val repository: SurahRepository,
    private val playerRepository: PlayerRepository,
    private val reciterRepository: ReciterRepository,
    private val networkObserver: NetworkConnectivityObserver,
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Player Error", "${exception.message}")
    }

    private var defaultReciter =
        Reciter(
            id = 1,
            arabicName = "عبدالباسط عبدالصمد",
            englishName = "AbdulBaset AbdulSamad",
            image = "https://upload.wikimedia.org/wikipedia/commons/5/55/Abdelbasset-abdessamad-27.jpg"
        )

    val playerState = mutableStateOf(PlayerSurah(reciter = defaultReciter))

    private var mediaController: MediaController? = null

    var queuePlaylist = mutableListOf<MediaItem>()

    private val _playPauseIcon = MutableStateFlow(R.drawable.outline_play_arrow_24)
    val playPauseIcon: StateFlow<Int> = _playPauseIcon

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _positionPercentage = MutableStateFlow(0f)
    val positionPercentage: StateFlow<Float> = _positionPercentage

    private val _currentBottomSheet = MutableStateFlow<BottomSheetType>(BottomSheetType.None)
    val currentBottomSheet: StateFlow<BottomSheetType> = _currentBottomSheet

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration


    var isCached = false

    init {

        viewModelScope.launch {
            changeDefaultReciter()
        }


        viewModelScope.launch(errorHandler) {
            networkObserver.observe().collect {
                if (it == NetworkStatus.Available) {
                    restoreCachedState()
                    if (isCached) {
                        applyCachedState()
                    }
                }
            }


        }
        setupMediaController()

    }

    private fun setupMediaController() {

        mediaControllerFuture.addListener(
            {
                mediaController = mediaControllerFuture.get().apply {
                    addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            savePlayer()
                            if (isPlaying) {
                                _playPauseIcon.update { R.drawable.outline_pause_24 }
                            } else {
                                _playPauseIcon.update { R.drawable.outline_play_arrow_24 }
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
                            if (playbackState == Player.STATE_READY && isCached) {
                                restoreCachedPosition()
                            }
                            startPositionUpdates()
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
                }
            }, MoreExecutors.directExecutor()
        )


    }

    private fun updatePlaybackState() {
        val position = mediaController?.currentPosition ?: 0L
        val duration = mediaController?.duration?.takeIf { it > 0 } ?: 0L
        val progress = if (duration > 0) position.toFloat() / duration else 0f
        _currentPosition.value = position
        _positionPercentage.value = progress
        _duration.value = duration
    }

    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (isActive) {
                updatePlaybackState()
                delay(1000L)
            }
        }
    }

    private fun restoreCachedPosition() {
        val player = playerState.value
        val maxPosition = mediaController?.duration?.coerceAtLeast(0L)
        val targetPosition = player.position.coerceIn(0L, maxPosition)
        if (targetPosition > 0) {
            mediaController?.seekTo(targetPosition)
            isCached = false
        }
    }

    private fun applyCachedState() {
        val player = playerState.value

        if (player.surah != null) {
            viewModelScope.launch {

                val metadataData = getMediaURL(
                    surahID = player.surah.id,
                    reciterID = player.reciter.id,
                    recitationID = player.recitationID
                )
                val metadata = setMetadata(metadataData)
                mediaController?.setMediaItem(metadata)
                mediaController?.prepare()

            }
        }
    }

    private suspend fun restoreCachedState() {
        playerRepository.getPlayer()?.let { cachedPlayer ->
            playerState.value = cachedPlayer
            isCached = true
            Log.d("Player Reciter", "Changed Player to Cache")
        }
    }

    private fun setMetadata(data: AudioData): MediaItem {
        val surahName = data.surah.arabicName
        val reciterName = data.recitation.reciter.arabicName
        val metadata: MediaMetadata = MediaMetadata.Builder().setTitle(surahName)
            .setGenre(data.recitation.id.toString())
            .setAlbumTitle(data.recitation.reciter.id.toString())
            .setAlbumArtist(data.recitation.reciter.image)
            .setArtist(reciterName).setArtworkUri(data.surah.image.toUri())
            .build()
        return MediaItem.Builder().setUri(data.url.toUri()).setMediaMetadata(metadata)
            .setMediaId(data.surah.id.toString()).build()
    }

    private fun playerSetItem(data: AudioData) {
        val mediaItem: MediaItem = setMetadata(data)
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    private suspend fun getMediaURL(surahID: Int, reciterID: Int, recitationID: Int?): AudioData {
        return repository.getSurahUrl(
            surahID = surahID,
            reciterID = reciterID,
            recitationID = recitationID
        ).response.also { data ->
            withContext(Dispatchers.Main) {
                playerState.value = playerState.value.copy(url = data.url)
            }
        }
    }

    private fun getQueueUrls(currentSurahID: Int, reciterID: Int) {
        val previousChapters =
            (1..3).map { currentSurahID - it }.filter { it > 0 }
        val nextChapters = (1..3).map { currentSurahID + it }.filter { it <=114 }

        val previousMediaItems = mutableSetOf<MediaItem>()

        val nextMediaItems = mutableSetOf<MediaItem>()

        val previousJobs = previousChapters.map { id ->
            viewModelScope.async(errorHandler) {
                val data: AudioData = repository.getSurahUrl(
                    surahID = id, reciterID = reciterID, recitationID = null
                ).response
                val mediaItem = setMetadata(data)
                previousMediaItems.add(mediaItem)
//                mediaController?.add
            }
        }

        val currentMediaItem = viewModelScope.async(errorHandler) {
            val data: AudioData = repository.getSurahUrl(
                surahID = currentSurahID, reciterID = reciterID, recitationID = null
            ).response
            setMetadata(data)
        }

        val nextJobs = nextChapters.map { id ->
            viewModelScope.async(errorHandler) {
                val data: AudioData = repository.getSurahUrl(
                    surahID = id, reciterID = reciterID, recitationID = null
                ).response
                val mediaItem = setMetadata(data)
                nextMediaItems.add(mediaItem)
//                mediaController?.addMediaItem(mediaItem)
            }
        }
        viewModelScope.launch {
            previousJobs.awaitAll()
            nextJobs.awaitAll()
            val currentItem = currentMediaItem.await()
            queuePlaylist =
                (previousMediaItems + currentItem + nextMediaItems).toMutableList()
            mediaController?.addMediaItems(queuePlaylist)

        }
    }

    fun fetchMediaUrl(rId: Int? = null) {
        val surahID: Int = playerState.value.surah!!.id
        val reciterID: Int = rId ?: defaultReciter.id
        val recitationID = if (rId != null) null else playerState.value.recitationID
        viewModelScope.launch {
            val mediaData = getMediaURL(surahID, reciterID, recitationID)
            playerSetItem(mediaData)
            getQueueUrls(surahID, reciterID)
        }

        mediaController?.play()
        isCached = false

    }

    fun playAudioData(data: AudioData) {
        playerSetItem(data)
        if (queuePlaylist.isEmpty()) {
            viewModelScope.launch {
                getQueueUrls(data.surah.id, data.recitation.reciter.id)

            }
        }
        mediaController?.play()
        isCached = false
    }

    fun playQueueItem(mediaItemIndex: Int) {
        Log.d(
            "Playlist",
            "MediaItem: ${mediaController?.getMediaItemAt(mediaItemIndex)?.mediaMetadata?.title}"
        )
        mediaController?.seekTo(mediaItemIndex, 0L)
    }

    fun handlePlayPause() {
        if (mediaController?.isPlaying == true) {
            pause()
        } else {
            mediaController?.play()
        }
    }

    fun seekToPosition(position: Float) {
        _positionPercentage.update { position }
        val scaledPosition = (position * (mediaController?.duration ?: 0L) / 1f).toLong()
        mediaController?.seekTo(scaledPosition)
    }

    fun seekNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun seekPrevious() {
        val currentSurahID: Int = playerState.value.surah?.id ?: 1
        if (currentSurahID > 1) {
            mediaController?.seekToPrevious()
            return
        }
        mediaController?.seekToDefaultPosition()
    }

    fun addNext(surahID: Int) {
        val currentMediaItemIndex: Int? = mediaController?.currentMediaItemIndex
        val reciterID: Int = playerState.value.reciter.id
        if (currentMediaItemIndex != null) {
            viewModelScope.launch {
                val data: AudioData = repository.getSurahUrl(
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

    fun pause() {
        mediaController?.pause()
    }

    fun clear() {
        viewModelScope.launch {
            reciterRepository.getDefaultReciter()!!.collectLatest {
                playerState.value =
                    playerState.value.copy(surah = null, recitationID = null, reciter = it)
                mediaController?.pause()
                savePlayer()
            }

        }
    }

    fun addMediaItem(surahID: Int) {
        val reciterID: Int = playerState.value.reciter.id
        viewModelScope.launch {
            val data: AudioData = repository.getSurahUrl(
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

    fun changeDefaultReciter() {
        viewModelScope.launch(errorHandler) {
            reciterRepository.getDefaultReciter()!!.collectLatest {
                Log.d("Choosen Reciter", "changeDefaultReciter: ${it}")
                defaultReciter = it
            }

        }
    }

    private fun savePlayer() {
        val player = playerState.value.copy(
            progress = _positionPercentage.value,
            position = _currentPosition.value
        )
        viewModelScope.launch {
            playerRepository.savePlayer(
                player
            )
        }
    }

    fun showBottomSheet(sheetType: BottomSheetType) {
        _currentBottomSheet.update { sheetType }
    }

    fun hideBottomSheet() {
        _currentBottomSheet.update { BottomSheetType.None }
    }

    fun changeSurah(surah: Surah) {
        playerState.value =
            playerState.value.copy(surah = surah, reciter = defaultReciter, recitationID = null)
        fetchMediaUrl()

    }

    fun changeReciter(reciter: Reciter) {
        playerState.value = playerState.value.copy(
            reciter = reciter, recitationID = null
        )
        fetchMediaUrl(rId = reciter.id)
    }

    fun changeRecitation(id: Int) {
        playerState.value = playerState.value.copy(
            recitationID = id
        )
    }

    fun changeShape(id: String, context: Context) {
        viewModelScope.launch {
            context.dataStore.updateData { settings ->
                settings.copy(shapeID = id)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.release()
        mediaController = null

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


