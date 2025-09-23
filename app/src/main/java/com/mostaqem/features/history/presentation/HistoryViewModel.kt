package com.mostaqem.features.history.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.NetworkStatus
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.history.data.AllFilter
import com.mostaqem.features.history.data.ChapterFilter
import com.mostaqem.features.history.data.HistoryState
import com.mostaqem.features.history.data.RecitersFilter
import com.mostaqem.features.history.data.SearchFilter
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.domain.ReciterRepository
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val surahDao: SurahDao,
    private val reciterDao: ReciterDao,
    private val surahRepository: SurahRepository,
    private val reciterRepository: ReciterRepository,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        Log.e("Surah Error", "${exception.message}")
    }
    private val _state = MutableStateFlow(HistoryState())
    val uiState: StateFlow<HistoryState> = _state

    val networkStatus: StateFlow<NetworkStatus> =
        networkConnectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Adjust timeout as needed
                initialValue = NetworkStatus.Unavailable // Or determine initial state synchronously
            )

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

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        if (query == ""){
            _state.value = _state.value.copy(queryList = emptyList())
            return
        }
        // You might want to add a debounce here to avoid excessive API calls
        search()
    }

    fun fetchData() {
        viewModelScope.launch(errorHandler) {
            _state.update { it.copy(randomSurah = Result.Error(error = DataError.Network.NO_INTERNET)) }
            _state.update { it.copy(randomSurah = Result.Loading) }
            _state.update { it.copy(randomSurah = surahRepository.getRandomSurahs(limit = 6)) }
        }
    }

    fun onFilterSelected(filter: SearchFilter) {
        _state.update { it.copy(selectedFilter = filter) }
        search()
    }

    fun search() {
        val state = _state.value
        if (state.searchQuery.isBlank()) {
            _state.value = state.copy(queryList = emptyList())
            return
        }
        viewModelScope.launch(errorHandler) {

            try {
                val results = when (state.selectedFilter) {
                    is AllFilter -> searchAll(state.searchQuery)
                    is ChapterFilter -> {
                        val chapters = surahRepository.getSurahs(state.searchQuery).response.surahData
                        listOf(ChapterFilter(displayName = "chapters", chapters = chapters))
                    }
                    is RecitersFilter -> {
                        val reciters = reciterRepository.getReciters(state.searchQuery).response.reciters.take(6)
                        listOf(RecitersFilter(displayName = "reciters", reciters = reciters))
                    }
                }
                _state.value = _state.value.copy(queryList = results)
            } catch (e: Exception) {
            }
//            val surahs = surahRepository.getSurahs(query).response.surahData
//            val reciters = reciterRepository.getReciters(query).response.reciters
//            _state.update {
//                it.copy(
//                    queryList = listOf(
//                        ChapterFilter(
//                            id = "chapters",
//                            displayName = "Chapters",
//                            chapters = surahs
//                        ),
//                        RecitersFilter(
//                            id = "reciters",
//                            displayName = "Reciters",
//                            reciters = reciters
//                        ),
//                    )
//
//                )
//            }
        }

    }

    private suspend fun searchAll(query: String): List<SearchFilter> {
        return coroutineScope {
            val chapters = async { surahRepository.getSurahs(query).response.surahData }
            val reciters = async { reciterRepository.getReciters(query).response.reciters.take(6) }
            listOf(
                ChapterFilter(displayName = "Chapters", chapters = chapters.await()),
                RecitersFilter(reciters = reciters.await(), displayName = "Reciters")
            )
        }
    }

}






