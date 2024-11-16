package com.mostaqem.screens.reciters.domain

import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Surah

sealed interface ReciterEvents{
    data class AddReciter(val reciter: Reciter) : ReciterEvents
    data class DeleteSurah(val reciter: Reciter) : ReciterEvents
}