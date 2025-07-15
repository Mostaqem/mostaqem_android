package com.mostaqem.features.offline.domain

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadCursor
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Requirements
import androidx.media3.exoplayer.scheduler.Requirements.NETWORK
import com.google.gson.Gson
import com.mostaqem.core.database.dao.DownloadedAudioDao
import com.mostaqem.features.offline.data.DownloadUiState
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.offline.data.DownloadedAudioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.concurrent.Executor
import kotlin.String
import kotlin.collections.Map

@OptIn(UnstableApi::class)
class OfflineManager
    (
    private val context: Context, private val downloadedAudioDao: DownloadedAudioDao
) {
    val downloadManager: DownloadManager by lazy {
        DownloadUtil.getDownloadManager(context)
    }
    private val gson = Gson()
    private val _downloadState = MutableStateFlow<Map<String, DownloadUiState>>(emptyMap())
    val downloadState: StateFlow<Map<String, DownloadUiState>> = _downloadState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isPolling = false

    init {
        updateDownloadState()
        startProgressPolling()

        downloadManager.addListener(object : DownloadManager.Listener {

            override fun onDownloadChanged(
                downloadManager: DownloadManager, download: Download, finalException: Exception?
            ) {
                val currentState = _downloadState.value.toMutableMap()
                val downloadId = download.request.id
                val data = download.request.data
                val audio: AudioData =
                    gson.fromJson(data.toString(Charsets.UTF_8), AudioData::class.java)
                val newState = DownloadUiState(
                    downloadID = downloadId,
                    progress = download.percentDownloaded / 100,
                    downloadAudio = audio,
                    state = download.state
                )
                currentState[downloadId] = newState
                _downloadState.value = currentState

                when (download.state) {
                    Download.STATE_COMPLETED -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val entity = DownloadedAudioEntity(
                                surahID = audio.surah.id.toString(),
                                size = download.bytesDownloaded,
                                title = audio.surah.arabicName,
                                reciter = audio.recitation.reciter.arabicName,
                                recitationID = audio.recitationID.toString(),
                                remoteUrl = audio.url,
                                id = download.request.id,
                                reciterID = audio.recitation.reciter.id.toString()
                            )
                            downloadedAudioDao.saveDownloadedAudio(entity)

                        }
                    }

                    Download.STATE_DOWNLOADING -> {
                        Log.d("Download", "onDownloadChanged: Downloading: ${download.request.id}")

                    }

                    else -> null

                }
                super.onDownloadChanged(downloadManager, download, finalException)
            }

            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
                CoroutineScope(Dispatchers.IO).launch {
                    downloadedAudioDao.deleteDownloadedAudio(download.request.id)
                }
                updateDownloadState()
                super.onDownloadRemoved(downloadManager, download)
            }
        })
    }


    private fun startProgressPolling() {
        if (isPolling) return
        isPolling = true

        scope.launch {
            while (isPolling) {
                updateDownloadState()
                delay(500)
            }
        }
    }


    private fun updateDownloadState() {
        val downloads = mutableMapOf<String, DownloadUiState>()
        val cursor: DownloadCursor = downloadManager.downloadIndex.getDownloads()
        while (cursor.moveToNext()) {
            val download = cursor.download
            val data = download.request.data
            val audio: AudioData =
                gson.fromJson(data.toString(Charsets.UTF_8), AudioData::class.java)
            downloads[download.request.id] = DownloadUiState(
                downloadID = download.request.id,
                progress = download.percentDownloaded / 100,
                downloadAudio = audio,
                state = download.state
            )
        }
        _downloadState.value = downloads

        cursor.close()
    }

    fun startDownload(audio: AudioData) {
        val downloadID = "${audio.surah.id}-${audio.recitationID}"
        val customData = gson.toJson(audio).toByteArray(Charsets.UTF_8)

        val downloadRequest = DownloadRequest.Builder(
            downloadID, audio.url.toUri()
        ).setMimeType("audio/mpeg").setData(customData).build()
        DownloadService.sendAddDownload(
            context, AppDownloadService::class.java, downloadRequest, true
        )

    }

    suspend fun startDownloadList(audios: List<AudioData>) {
        val downloadedAudios = getAllDownloads().first()
        val currentDownloads = downloadManager.currentDownloads

        audios.forEach { audio ->
            val downloadID = "${audio.surah.id}-${audio.recitationID}"

            val isDownloaded = downloadedAudios.any { it.id == downloadID }
            val isDownloading = currentDownloads.any { it.request.id == downloadID }

            if (!isDownloaded && !isDownloading) {
                val customData = gson.toJson(audio).toByteArray(Charsets.UTF_8)

                val downloadRequest = DownloadRequest.Builder(
                    downloadID, audio.url.toUri()
                ).setMimeType("audio/mpeg").setData(customData).build()

                DownloadService.sendAddDownload(
                    context, AppDownloadService::class.java, downloadRequest, true
                )
            }
        }
    }

    fun removeDownload(audioId: String) {
        DownloadService.sendRemoveDownload(
            context, AppDownloadService::class.java, audioId, false
        )
    }

    fun removeAllDownloads() {
        DownloadService.sendRemoveAllDownloads(
            context, AppDownloadService::class.java, false
        )

    }


    fun pauseDownload() {
        DownloadService.sendPauseDownloads(
            context, AppDownloadService::class.java, false
        )
    }

    fun resumeDownload() {
        DownloadService.sendResumeDownloads(
            context, AppDownloadService::class.java, false
        )
    }

    fun getAllDownloads(): Flow<List<DownloadedAudioEntity>> {
        val downloaded = downloadedAudioDao.getAllDownloadedAudios()
        return downloaded
    }


    fun getDownloadProgress(surahID: Int, recitationID: Int): Flow<Float> {
        Log.d("Download", "getDownloadProgress: ${downloadManager.currentDownloads}")
        return flow {
            while (true) {
                val progress =
                    downloadManager.currentDownloads.find { it.request.id == "${surahID}-${recitationID}" }?.percentDownloaded
                        ?: 0f
                emit(progress)
                if (progress == 100f) break
                delay(100)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getDownloadedMediaItem(surahID: Int, recitationID: Int): MediaItem? {
        return downloadManager.downloadIndex.getDownload("${surahID}-${recitationID}")
            ?.takeIf { it.state == Download.STATE_COMPLETED }?.request?.toMediaItem()
    }


}