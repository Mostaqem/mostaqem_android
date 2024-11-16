package com.mostaqem.core.database.events

import com.mostaqem.screens.surahs.data.Surah

sealed interface SurahEvents {
    data class AddSurah(val surah: Surah) : SurahEvents
    data class DeleteSurah(val surah: Surah) : SurahEvents
}