package com.mostaqem.screens.home.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.NetworkResult
import com.mostaqem.core.network.models.NetworkStatus
import com.mostaqem.screens.home.domain.HomeRepository
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.AudioData
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.data.SurahRepositoryImpl
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val surahDao: SurahDao,
    private val reciterDao: ReciterDao,
    private val surahRepository: SurahRepository,
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
    private var _randomSurahs =
        MutableStateFlow<NetworkResult<List<AudioData>>>(NetworkResult.Loading())
    var randomSurah: StateFlow<NetworkResult<List<AudioData>>> = _randomSurahs

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
        fetchData()

    }

    fun fetchData(){
        viewModelScope.launch(errorHandler) {
            _randomSurahs.value = NetworkResult.Loading()
            _randomSurahs.value =
                surahRepository.getRandomSurahs(limit = 6)

        }
    }


}






