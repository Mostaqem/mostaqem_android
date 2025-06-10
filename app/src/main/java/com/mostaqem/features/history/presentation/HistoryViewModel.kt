package com.mostaqem.features.history.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.history.data.HistoryState
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val surahDao: SurahDao,
    private val reciterDao: ReciterDao,
    private val surahRepository: SurahRepository,
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Surah Error", "${exception.message}")
    }
    private val _state = MutableStateFlow(HistoryState())
    val uiState: StateFlow<HistoryState> = _state

    init {
        viewModelScope.launch(errorHandler) {
            _state.update { it.copy(loading = true) }

            surahDao.getSurahs().collect { surahs ->
                _state.update { it.copy(savedSurahs = surahs) }
                _state.update { it.copy(loading = false) }

            }

        }

        viewModelScope.launch(errorHandler) {
            _state.update { it.copy(loading = true) }

            reciterDao.getReciters().collect { reciters ->
                _state.update { it.copy(savedReciters = reciters) }
                _state.update { it.copy(loading = false) }

            }

        }
        fetchData()

    }

    fun fetchData() {

        viewModelScope.launch(errorHandler) {
            _state.update { it.copy(randomSurah = Result.Error(error = DataError.Network.NO_INTERNET)) }
            _state.update { it.copy(randomSurah = Result.Loading) }

            _state.update { it.copy(randomSurah = surahRepository.getRandomSurahs(limit = 6)) }

        }
    }


}






