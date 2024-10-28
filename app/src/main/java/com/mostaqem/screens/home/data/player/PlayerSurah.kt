package com.mostaqem.screens.home.data.player

import com.mostaqem.screens.home.data.reciter.Reciter
import com.mostaqem.screens.home.data.surah.Surah

val defaultReciter =
    Reciter(id = 1, arabicName = "عبدالباسط عبدالصمد", englishName = "AbdelBasit", image = "")

data class PlayerSurah(
    val surah: Surah? = null,
    val reciter: Reciter = defaultReciter,
    val url: String? = null
)
