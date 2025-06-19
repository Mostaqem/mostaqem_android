package com.mostaqem.features.history.data

import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah

sealed interface SearchFilter {
    val id: String
    val displayName: String
}

data class ChapterFilter(
    override val id: String = "chapters",
    override val displayName: String,
    val chapters: List<Surah> = emptyList()
) : SearchFilter

data class RecitersFilter(
    override val id: String = "reciters",
    override val displayName: String,
    val reciters: List<Reciter> = emptyList()
) : SearchFilter

data class AllFilter(
    override val id: String = "all",
    override val displayName: String = "All",
    val results: List<SearchFilter> = emptyList()
) :
    SearchFilter