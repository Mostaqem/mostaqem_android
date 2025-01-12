package com.example.history

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Reciter
import com.example.data.Surah
import com.example.database.dao.ReciterDao
import com.example.database.dao.SurahDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val surahDao: SurahDao,
    private val reciterDao: ReciterDao,
    ) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Surah Error", "${exception.message}")
    }
    private var _savedSurahs = MutableStateFlow<List<Surah>>(emptyList())
    var savedSurahs: StateFlow<List<Surah>> = _savedSurahs
    private var _savedReciters = MutableStateFlow<List<Reciter>>(emptyList())
    var savedReciters: StateFlow<List<Reciter>> = _savedReciters

    var loading = mutableStateOf<Boolean>(false)

    init {
        viewModelScope.launch(errorHandler) {
            loading.value = true

            surahDao.getSurahs().collect { surahs ->
                _savedSurahs.value = surahs
                loading.value = false
            }

        }
        viewModelScope.launch(errorHandler) {
            loading.value = true
            reciterDao.getReciters().collect { reciters ->
                _savedReciters.value = reciters
                loading.value = false

            }

        }

    }

}


