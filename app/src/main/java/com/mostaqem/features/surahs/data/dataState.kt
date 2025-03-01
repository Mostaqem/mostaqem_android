package com.mostaqem.features.surahs.data

import com.mostaqem.features.reciters.data.reciter.Reciter

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
