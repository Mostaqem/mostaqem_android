package com.mostaqem.features.offline.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.offline.data.DownloadUiState
import com.mostaqem.features.offline.data.DownloadedAudioEntity
import com.mostaqem.features.offline.domain.OfflineManager
import com.mostaqem.features.offline.domain.OfflineRepository
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class OfflineViewModel @Inject constructor(
    private val repository: OfflineRepository,
    private val manager: OfflineManager,
    private val surahRepository: SurahRepository

) : ViewModel() {
    val downloadedAudios: StateFlow<List<DownloadedAudioEntity>> =
        manager.getAllDownloads().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000)
        )

    val downloadUiState: StateFlow<Map<String, DownloadUiState>> = manager.downloadState


    private val _isPaused: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _isDownloading: MutableStateFlow<Boolean> = MutableStateFlow(value = true)
    val isDownloading: StateFlow<Boolean> = _isDownloading

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _loading

    fun getMemoryPercentage(): Float {
        return repository.calculateMemoryProgress()
    }

    fun delete(audioID: String) {
        repository.deleteFile(audioID)
        refresh()
    }

    private fun refresh() {
        downloadedAudios.value.sortedBy { -it.size }
    }

    fun removeDownload(audioID: String) {
        manager.removeDownload(audioID)
    }

    fun removeALl() {
        manager.removeAllDownloads()
    }

    fun pauseDownload() {
        _isPaused.value = true
        _isDownloading.value = false
        manager.pauseDownload()
    }

    fun resumeDownload() {
        _isPaused.value = false
        _isDownloading.value = true
        manager.resumeDownload()
    }

    fun downloadAll() {
        viewModelScope.launch {
            _loading.value = true
            val surahs = surahRepository.getAllSurahsUrls()
            if (surahs is Result.Success) {
                manager.startDownloadList(surahs.data)
            }
            _loading.value = false


        }

    }


}