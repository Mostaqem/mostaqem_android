package com.mostaqem.features.player.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mostaqem.R
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.NetworkStatus
import com.mostaqem.core.network.models.Result
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.features.language.domain.LanguageManager
import com.mostaqem.features.offline.domain.OfflineRepository
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.player.data.BottomSheetType
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.player.domain.repository.PlayerRepository
import com.mostaqem.features.reciters.data.RecitationData
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val playerRepository: PlayerRepository,
    private val personalizationRepository: PersonalizationRepository,
    private val networkObserver: NetworkConnectivityObserver,
    private val offlineRepository: OfflineRepository,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Player Error", "${exception.message}")
    }

    private var defaultReciter = Reciter(
        id = 1,
        arabicName = "عبدالباسط عبدالصمد",
        englishName = "AbdulBaset AbdulSamad",
        image = "https://upload.wikimedia.org/wikipedia/ar/7/73/%D8%B5%D9%88%D8%B1%D8%A9_%D8%B4%D8%AE%D8%B5%D9%8A%D8%A9_%D8%B9%D8%A8%D8%AF_%D8%A7%D9%84%D8%A8%D8%A7%D8%B3%D8%B7_%D8%B9%D8%A8%D8%AF_%D8%A7%D9%84%D8%B5%D9%85%D8%AF.png"
    )

    val playerState = mutableStateOf(PlayerSurah(reciter = defaultReciter))

    private var mediaController: MediaController? = null

    var queuePlaylist: MutableList<MediaItem> = mutableListOf<MediaItem>()

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

        viewModelScope.launch {
            restoreCachedState()
            Log.d("Player", "Local Player : ${playerState.value.isLocal}")
            if (!playerState.value.isLocal) {
                networkObserver.observe().collect {
                    if (it == NetworkStatus.Available) {
                        if (isCached) applyCachedState()

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

                        override fun onPlayerError(error: PlaybackException) {
                            Log.e("PlayerError", "Playback error: ${error.errorCode}", error)

                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            if (mediaItem != null) {
                                playerState.value = playerState.value.copy(
                                    surah = Surah(
                                        id = mediaItem.mediaId.toInt(),
                                        image = mediaItem.mediaMetadata.artworkUri.toString(),
                                        arabicName = mediaItem.mediaMetadata.title.toString(),
                                        complexName = "",
                                        versusCount = 0,
                                        revelationPlace = ""
                                    ),
                                    reciter = Reciter(
                                        id = mediaItem.mediaMetadata.albumTitle.toString()
                                            .toInt(),
                                        arabicName = mediaItem.mediaMetadata.artist.toString(),
                                        image = "",
                                        englishName = ""
                                    ),
                                    recitationID = mediaItem.mediaMetadata.albumArtist.toString()
                                        .toInt()
                                )

                            }

                            super.onMediaItemTransition(mediaItem, reason)
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            startPositionUpdates()
                            if (playbackState == Player.STATE_ENDED) {
                                val currentIndex = mediaController?.currentMediaItemIndex
                                val queueSize = mediaController?.mediaItemCount ?: 0
                                if (currentIndex == queueSize - 1 && !playerState.value.isLocal) {
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

    fun applyCachedState() {
        val player = playerState.value
        Log.d("Player", "Local Player : ${player.isLocal}")
        isCached = false
        if (player.surah != null) {
            if (!player.isLocal) {
                viewModelScope.launch {
                    val metadataData = playerRepository.getMediaURL(
                        surahID = player.surah.id,
                        reciterID = player.reciter.id,
                        recitationID = player.recitationID
                    )
                    if (metadataData is Result.Success) {
                        val metadata = setMetadata(metadataData.data.response)
                        mediaController?.setMediaItem(metadata, player.position)
                        mediaController?.prepare()
                        getQueueUrls(
                            currentSurahID = player.surah.id,
                            reciterID = player.reciter.id
                        )
                    }


                }
            } else {
                val data = AudioData(
                    url = player.url!!,
                    surah = player.surah,
                    recitationID = player.recitationID!!,
                    recitation = RecitationData(
                        reciter = player.reciter,
                        id = player.recitationID,
                        englishName = player.reciter.englishName,
                        reciterID = player.reciter.id,
                        name = ""
                    ),
                )
                val mediaItem = setMetadata(data)
                mediaController?.setMediaItem(mediaItem, player.position)
                mediaController?.prepare()


            }
        }
    }

    private suspend fun restoreCachedState() {
        playerRepository.getPlayer()?.let { cachedPlayer ->
            playerState.value = cachedPlayer
            Log.d("Player Reciter", "Changed Player to Cache")
        }
        isCached = true
    }

    private fun setMetadata(data: AudioData): MediaItem {
        val isDefaultEnglish = languageManager.getLanguageCode() == "en"
        val surahName = if (isDefaultEnglish) data.surah.complexName else data.surah.arabicName
        val reciterName =
            if (isDefaultEnglish) data.recitation.reciter.englishName else data.recitation.reciter.arabicName
        val metadata: MediaMetadata =
            MediaMetadata.Builder().setTitle(surahName)
                .setAlbumTitle(data.recitation.reciter.id.toString())
                .setAlbumArtist(data.recitation.id.toString()).setArtist(reciterName)
                .setArtworkUri(data.surah.image.toUri()).build()
        return MediaItem.Builder().setUri(data.url.toUri()).setMediaMetadata(metadata)
            .setMediaId(data.surah.id.toString()).build()
    }

    private fun setLocalMetadata(surahID: Int, recitationID: Int): MediaItem? {
        val data: AudioData? = offlineRepository.getFileURL(surahID, recitationID)

        if (data != null) {
            val metadata: MediaMetadata =
                MediaMetadata.Builder()
                    .setTitle(data.surah.arabicName)
                    .setAlbumTitle(data.recitation.reciter.id.toString())
                    .setAlbumArtist(data.recitation.id.toString())
                    .setArtist(data.recitation.reciter.arabicName)
                    .setArtworkUri(data.surah.image.toUri()).build()
            return MediaItem.Builder().setUri(data.url.toUri()).setMediaMetadata(metadata)
                .setMediaId(data.surah.id.toString()).build()
        }
        return null
    }

    private fun playerSetItem(data: AudioData) {
        val mediaItem: MediaItem = setMetadata(data)
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
    }

    private fun getQueueUrls(currentSurahID: Int, reciterID: Int) {
        val previousChapters = (1..3).map { currentSurahID - it }.filter { it > 0 }
        val nextChapters = (1..3).map { currentSurahID + it }.filter { it <= 114 }
        val previousMediaItems = mutableSetOf<MediaItem>()
        val currentMediaItem: MediaItem = mediaController?.currentMediaItem!!

        val nextMediaItems = mutableSetOf<MediaItem>()

        val previousJobs = previousChapters.map { id ->
            viewModelScope.async(errorHandler) {
                val recitationID = personalizationRepository.getDefaultRecitationID().first()
                val isSurahDownloaded =
                    offlineRepository.isSurahDownloaded(id, recitationID)
                val playDownloaded = offlineRepository.getPlayDownloadedOption().first()
                if (isSurahDownloaded && playDownloaded) {
                    val metadata = setLocalMetadata(id, recitationID)!!
                    previousMediaItems.add(metadata)
                } else {
                    val data = playerRepository.getMediaURL(
                        surahID = id, reciterID = reciterID, recitationID = recitationID
                    )
                    if (data is Result.Success) {
                        val mediaItem = setMetadata(data.data.response)
                        previousMediaItems.add(mediaItem)
                    }
                }

            }
        }


        val nextJobs = nextChapters.map { id ->
            viewModelScope.async(errorHandler) {
                val recitationID = personalizationRepository.getDefaultRecitationID().first()
                val isSurahDownloaded =
                    offlineRepository.isSurahDownloaded(id, recitationID)
                val playDownloaded = offlineRepository.getPlayDownloadedOption().first()
                if (isSurahDownloaded && playDownloaded) {
                    val metadata = setLocalMetadata(id, recitationID)!!
                    nextMediaItems.add(metadata)
                } else {
                    val data = playerRepository.getMediaURL(
                        surahID = id, reciterID = reciterID, recitationID = recitationID
                    )
                    if (data is Result.Success) {
                        val mediaItem = setMetadata(data.data.response)
                        nextMediaItems.add(mediaItem)
                    }
                }

            }
        }
        viewModelScope.launch {
            previousJobs.awaitAll()
            nextJobs.awaitAll()
            queuePlaylist =
                (previousMediaItems.reversed() + currentMediaItem + nextMediaItems).toMutableList()
            mediaController?.run {
                clearMediaItems()
                addMediaItems(queuePlaylist)
                val currentIndex = previousMediaItems.size
                seekTo(currentIndex, 0L)
            }
        }
    }

    fun fetchMediaUrl(reciterId: Int? = null, surahId: Int? = null, recID: Int? = null) {

        val surahID: Int = surahId ?: playerState.value.surah!!.id
        val reciterID: Int = reciterId ?: defaultReciter.id
        viewModelScope.launch {
            val recitationID = recID ?: personalizationRepository.getDefaultRecitationID()
                .first()

            val isSurahDownloaded = offlineRepository.isSurahDownloaded(surahID, recitationID)
            val playDownloaded = offlineRepository.getPlayDownloadedOption().first()
            isCached = false

            if (isSurahDownloaded && playDownloaded) {

                val mediaItem = setLocalMetadata(surahID, recitationID)

                mediaController?.setMediaItem(mediaItem!!)
                mediaController?.prepare()
                playerState.value = playerState.value.copy(isLocal = true)

            } else {
                val networkStatus = networkObserver.observe().first()
                if (networkStatus == NetworkStatus.Available) {
                    val mediaData = playerRepository.getMediaURL(surahID, reciterID, recID)
                    if (mediaData is Result.Success) {
                        playerSetItem(mediaData.data.response)
                        getQueueUrls(surahID, reciterID)
                        playerState.value = playerState.value.copy(isLocal = false)
                    }


                }
            }
            mediaController?.play()

        }
    }

    fun playAudioData(data: AudioData) {
        playerSetItem(data)
        playerState.value = playerState.value.copy(url = data.url)
        mediaController?.play()
        isCached = false
        viewModelScope.launch {
            getQueueUrls(data.surah.id, data.recitation.reciter.id)

        }

    }

    fun playQueueItem(mediaItemIndex: Int) {
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

    fun addNext(surahID: Int, reciterID: Int? = null, recitationID: Int? = null) {
        val currentMediaItemIndex: Int? = mediaController?.currentMediaItemIndex
        val reID: Int = reciterID ?: defaultReciter.id
        if (currentMediaItemIndex != null) {
            viewModelScope.launch {
                val recID: Int =
                    recitationID ?: personalizationRepository.getDefaultRecitationID().first()
                val isSurahDownloaded = offlineRepository.isSurahDownloaded(surahID, recID)
                val playDownloaded = offlineRepository.getPlayDownloadedOption().first()
                if (isSurahDownloaded && playDownloaded) {
                    val mediaItem = setLocalMetadata(surahID, recID)
                    mediaController?.addMediaItem(currentMediaItemIndex + 1, mediaItem!!)
                    queuePlaylist.add(currentMediaItemIndex, mediaItem!!)
                    SnackbarController.sendEvent(
                        events = SnackbarEvents(
                            message = "تم اضافة السورة في التالي"
                        )
                    )
                } else {
                    networkObserver.observe().collect {
                        if (it == NetworkStatus.Available) {
                            val data = playerRepository.getMediaURL(
                                surahID = surahID, reciterID = reID, recitationID = recID
                            )
                            if (data is Result.Success) {
                                val mediaItem = setMetadata(data.data.response)
                                mediaController?.addMediaItem(currentMediaItemIndex + 1, mediaItem)
                                queuePlaylist.add(currentMediaItemIndex, mediaItem)

                            }
                            SnackbarController.sendEvent(
                                events = SnackbarEvents(
                                    message = "تم اضافة السورة في التالي"
                                )
                            )
                        } else {
                            SnackbarController.sendEvent(
                                events = SnackbarEvents(
                                    message = "لا يوجد انترنت!"
                                )
                            )
                        }
                    }


                }


            }
        }
    }

    fun pause() {
        mediaController?.pause()
    }

    fun clear() {
        playerState.value = playerState.value.copy(surah = null)
        savePlayer()
        mediaController?.clearMediaItems()
    }

    fun addMediaItem(surahID: Int, reciterID: Int? = null, recitationID: Int? = null) {
        val reID: Int = reciterID ?: defaultReciter.id
        viewModelScope.launch {
            val recID: Int =
                recitationID ?: personalizationRepository.getDefaultRecitationID().first()
            val isSurahDownloaded = offlineRepository.isSurahDownloaded(surahID, recID)
            val playDownloaded = offlineRepository.getPlayDownloadedOption().first()
            if (isSurahDownloaded && playDownloaded) {
                val mediaItem = setLocalMetadata(surahID, recID)
                mediaController?.addMediaItem(mediaItem!!)
                queuePlaylist.add(mediaItem!!)
                SnackbarController.sendEvent(
                    events = SnackbarEvents(
                        message = "تم اضافة السورة في قائمة التشغيل"
                    )
                )

            } else {
                networkObserver.observe().collect {
                    if (it == NetworkStatus.Available) {
                        val data = playerRepository.getMediaURL(
                            surahID = surahID, reciterID = reID, recitationID = recID
                        )
                        if (data is Result.Success) {
                            val mediaItem = setMetadata(data.data.response)
                            mediaController?.addMediaItem(mediaItem)
                            queuePlaylist.add(mediaItem)
                        }

                        SnackbarController.sendEvent(
                            events = SnackbarEvents(
                                message = "تم اضافة السورة في قائمة التشغيل"
                            )
                        )
                    } else {
                        SnackbarController.sendEvent(
                            events = SnackbarEvents(
                                message = "لا يوجد انترنت!"
                            )
                        )
                    }
                }


            }


        }
    }

    fun changeDefaultReciter() {
        viewModelScope.launch(errorHandler) {
            personalizationRepository.getDefaultReciter().collectLatest {
                defaultReciter = it
            }

        }
    }

    private fun savePlayer() {
        val player = playerState.value.copy(
            progress = _positionPercentage.value, position = _currentPosition.value
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

        fetchMediaUrl(surahId = surah.id)


    }

    fun changeReciter(reciter: Reciter) {
        if (playerState.value.surah != null) {
            playerState.value = playerState.value.copy(
                reciter = reciter, isLocal = false
            )
            fetchMediaUrl(reciterId = reciter.id)
        } else {
            playerState.value = playerState.value.copy(
                reciter = reciter, isLocal = false
            )
            fetchMediaUrl(reciterId = reciter.id, surahId = 1)
        }
    }


    fun changeRecitation(id: Int) {
        playerState.value = playerState.value.copy(
            recitationID = id,
            isLocal = false
        )
        fetchMediaUrl(recID = id)

    }

    fun download() {
        val currentMediaItem = mediaController?.currentMediaItem
        if (currentMediaItem == null || currentMediaItem.localConfiguration == null) return
        viewModelScope.launch {
            delay(1000)
            offlineRepository.downloadAudio(data = currentMediaItem)

        }
    }


    fun localPlay(data: AudioData) {
        playerState.value = playerState.value.copy(isLocal = true, url = data.url)
        val mediaItem =
            setLocalMetadata(surahID = data.surah.id, recitationID = data.recitationID)!!
        mediaController?.setMediaItem(mediaItem)
        mediaController?.prepare()
        mediaController?.play()
        isCached = false
    }

    fun isCurrentSurahDownloaded(): Boolean {
        val surahID = playerState.value.surah?.id ?: 0
        val recitationID = playerState.value.recitationID ?: 0
        return offlineRepository.isSurahDownloaded(surahID = surahID, recitationID = recitationID)
    }


    override fun onCleared() {
        mediaController?.release()
        mediaController = null
        super.onCleared()
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


