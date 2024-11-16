package com.mostaqem.screens.reciters.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.domain.ReciterEvents
import com.mostaqem.screens.reciters.domain.ReciterRepository
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReciterViewModel @Inject constructor(
    private val repository: ReciterRepository,
    private val dao: ReciterDao
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Reciter Error", "${exception.message}")
    }
    private val _reciterState: MutableStateFlow<PagingData<Reciter>> =
        MutableStateFlow(value = PagingData.empty())
    val reciterState: StateFlow<PagingData<Reciter>> = _reciterState

    init {
        viewModelScope.launch(errorHandler) {
            repository.getRemoteReciters().distinctUntilChanged().cachedIn(viewModelScope).collect {
                _reciterState.value = it
            }
        }
    }

    fun onReciterEvents(event: ReciterEvents) {
        when (event) {
            is ReciterEvents.AddReciter -> {
                viewModelScope.launch {
                    dao.insertReciter(event.reciter)
                }
            }

            is ReciterEvents.DeleteSurah -> {
                viewModelScope.launch {
                    dao.deleteReciter(event.reciter)
                }
            }
        }
    }

}