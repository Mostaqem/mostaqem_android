package com.mostaqem.screens.reciters.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.screens.reciters.data.RecitationData
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.domain.ReciterEvents
import com.mostaqem.screens.reciters.domain.ReciterRepository
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

    private val _recitationState: MutableStateFlow<List<RecitationData>> =
        MutableStateFlow(value = emptyList())
    val recitationState: StateFlow<List<RecitationData>> = _recitationState


    init {
        viewModelScope.launch(errorHandler) {
            repository.getRemoteReciters(null).distinctUntilChanged().cachedIn(viewModelScope)
                .collect {
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

    fun onSearchReciters(query: String?) {
        viewModelScope.launch(errorHandler) {
            repository.getRemoteReciters(query).distinctUntilChanged().cachedIn(viewModelScope)
                .collect {
                    _reciterState.value = it
                }
        }
    }

    fun getRecitations(reciterID: Int){
        viewModelScope.launch(errorHandler) {
            val recitations = repository.getRemoteRecitations(reciterID)
            _recitationState.value = recitations.response

        }
    }


}