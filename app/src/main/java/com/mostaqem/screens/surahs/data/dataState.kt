package com.mostaqem.screens.surahs.data

import com.mostaqem.screens.reciters.data.reciter.Reciter

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
