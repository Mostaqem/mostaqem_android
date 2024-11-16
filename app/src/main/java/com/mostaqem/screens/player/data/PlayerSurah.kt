package com.mostaqem.screens.player.data

import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Surah

val defaultReciter =
    Reciter(
        id = 1,
        arabicName = "عبدالباسط عبدالصمد",
        englishName = "AbdulBaset AbdulSamad",
        image = "https://upload.wikimedia.org/wikipedia/commons/5/55/Abdelbasset-abdessamad-27.jpg"
    )

data class PlayerSurah(
    val surah: Surah? = null,
    val reciter: Reciter = defaultReciter,
    val url: String? = null
)
