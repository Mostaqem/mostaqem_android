package com.mostaqem.features.reciters.domain

import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah

sealed interface ReciterEvents{
    data class AddReciter(val reciter: Reciter) : ReciterEvents
    data class DeleteSurah(val reciter: Reciter) : ReciterEvents
}