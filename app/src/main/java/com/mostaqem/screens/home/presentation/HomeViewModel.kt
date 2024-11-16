package com.mostaqem.screens.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.screens.home.domain.HomeRepository
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Surah
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

    init {

        viewModelScope.launch(errorHandler) {
            surahDao.getSurahs().collect { surahs ->
                _savedSurahs.value = surahs
            }
        }
        viewModelScope.launch(errorHandler) {
            reciterDao.getReciters().collect { reciters ->
                _savedReciters.value = reciters
            }
        }

    }


}





