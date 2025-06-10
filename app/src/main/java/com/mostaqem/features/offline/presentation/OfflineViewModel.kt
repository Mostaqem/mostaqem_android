package com.mostaqem.features.offline.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.features.offline.domain.OfflineRepository
import com.mostaqem.features.surahs.data.AudioData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfflineViewModel @Inject constructor(
    private val repository: OfflineRepository
) : ViewModel() {
    private val _downloaded: MutableStateFlow<List<AudioData>> =
        MutableStateFlow(value = emptyList())
    val downloaded: StateFlow<List<AudioData>> = _downloaded

    init {
        viewModelScope.launch {
            val localFiles = repository.getAudioDataFiles()
            _downloaded.value = localFiles.sortedBy { -it.size }
        }

    }

    fun getMemoryPercentage(): Float {
        return repository.calculateMemoryProgress()
    }

    fun delete(path: String) {
        repository.deleteFile(path)
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val localFiles = repository.getAudioDataFiles()
            _downloaded.value = localFiles.sortedBy { -it.size }
        }
    }

    fun changePlayOption(value: Boolean) {
        viewModelScope.launch {
            repository.changePlayOption(value)
        }
    }
}