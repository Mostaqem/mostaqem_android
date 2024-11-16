package com.mostaqem.screens.surahs.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.data.SurahState
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahsViewModel @Inject constructor(
    private val repository: SurahRepository,
    private val dao: SurahDao

) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Surah Error", "${exception.message}")
    }

    private val _surahState: MutableStateFlow<PagingData<Surah>> =
        MutableStateFlow(value = PagingData.empty())
    val surahState: StateFlow<PagingData<Surah>> = _surahState

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        viewModelScope.launch(errorHandler) {
            repository.getRemoteSurahs().distinctUntilChanged().cachedIn(viewModelScope).collect {
                _surahState.value = it
            }
        }
    }


    fun pullRefresh() {
        _isRefreshing.update { true }
        viewModelScope.launch(errorHandler) {
            repository.getRemoteSurahs().distinctUntilChanged().cachedIn(viewModelScope)
                .collect {
                    _surahState.value = it
                }

            _isRefreshing.update { false }

        }

    }

    fun onSurahEvents(event: SurahEvents) {
        when (event) {
            is SurahEvents.AddSurah -> {
                viewModelScope.launch {
                    dao.insertSurah(event.surah)
                }
            }

            is SurahEvents.DeleteSurah -> {
                viewModelScope.launch {
                    dao.deleteSurah(event.surah)
                }
            }
        }
    }
}
