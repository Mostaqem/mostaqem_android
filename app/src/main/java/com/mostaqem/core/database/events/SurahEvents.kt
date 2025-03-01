package com.mostaqem.core.database.events

import com.mostaqem.features.surahs.data.Surah

sealed interface SurahEvents {
    data class AddSurah(val surah: Surah) : SurahEvents
    data class DeleteSurah(val surah: Surah) : SurahEvents
}