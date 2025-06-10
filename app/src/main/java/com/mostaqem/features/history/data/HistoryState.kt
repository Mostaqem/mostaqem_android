package com.mostaqem.features.history.data

import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah

data class HistoryState(
    val savedSurahs: List<Surah> = emptyList(),
    val savedReciters: List<Reciter> = emptyList(),
    val loading: Boolean = false,
    val randomSurah: Result<List<AudioData>, DataError.Network> = Result.Loading
)
