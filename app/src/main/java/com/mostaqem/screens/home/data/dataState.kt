package com.mostaqem.screens.home.data

import com.mostaqem.screens.home.data.reciter.Reciter
import com.mostaqem.screens.home.data.surah.Surah

data class SurahState(
    val surahs: List<Surah>,
    val isLoading: Boolean,
    val isError: String? = null
)

data class ReciterState(
    val reciters: List<Reciter>,
    val isLoading: Boolean,
    val isError: String? = null
)
