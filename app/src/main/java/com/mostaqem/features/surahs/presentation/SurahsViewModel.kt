package com.mostaqem.features.surahs.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.features.offline.domain.OfflineRepository
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.data.SurahSortBy
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahsViewModel @Inject constructor(
    private val repository: SurahRepository,
    private val dao: SurahDao,
    private val offlineRepository: OfflineRepository,
    private val personalizationRepository: PersonalizationRepository,
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Surah Error", "${exception.message}")
    }

    val appSettings: StateFlow<AppSettings> = personalizationRepository.appSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val surahState: StateFlow<PagingData<Surah>> =
        appSettings.map { it.sortBy }.distinctUntilChanged().flatMapLatest {
            repository.getRemoteSurahs(it).cachedIn(viewModelScope)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )


    val queryState = mutableStateOf<List<Surah>>(emptyList())

    private val _downloaded: MutableStateFlow<List<AudioData>> =
        MutableStateFlow(value = emptyList())
    val downloaded: StateFlow<List<AudioData>> = _downloaded


    private val _loading: MutableStateFlow<Boolean> =
        MutableStateFlow(value = false)
    val loading: StateFlow<Boolean> = _loading


    init {
        viewModelScope.launch {
            val localFiles = offlineRepository.getAudioDataFiles()
            _downloaded.value = localFiles
        }
    }


    fun onSurahEvents(event: SurahEvents) {
        when (event) {
            is SurahEvents.AddSurah -> {
                viewModelScope.launch {
                    dao.insertSurah(event.surah)
                    dao.updateSurah(event.surah.copy(lastAccessed = System.currentTimeMillis()))
                }
            }

            is SurahEvents.DeleteSurah -> {
                viewModelScope.launch {
                    dao.deleteSurah(event.surah)
                }
            }
        }
    }


    fun setSortBy(value: SurahSortBy) {
        viewModelScope.launch(errorHandler) {
            personalizationRepository.changeSortBy(value)
            Log.d("SortValue", "setSortBy: ${value}")
        }
    }


    fun searchSurahs(query: String?) {
        viewModelScope.launch(errorHandler) {
            _loading.update { true }
            val surahs = repository.getSurahs(query).response.surahData
            queryState.value = surahs
            _loading.update { false }

        }
    }

    fun displayDownloaded() {
        viewModelScope.launch {
            val localFiles = offlineRepository.getAudioDataFiles()
            _downloaded.value = localFiles
        }
    }


}

