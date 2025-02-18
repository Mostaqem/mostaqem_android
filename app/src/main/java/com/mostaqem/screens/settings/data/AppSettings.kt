package com.mostaqem.screens.settings.data

import com.mostaqem.screens.reciters.data.reciter.Reciter
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val shapeID: String = "rect",
    val language: Language = Language.ARABIC,
    val reciterSaved: Reciter = Reciter(
        id = 1,
        arabicName = "عبدالباسط عبدالصمد",
        englishName = "AbdulBaset AbdulSamad",
        image = "https://upload.wikimedia.org/wikipedia/commons/5/55/Abdelbasset-abdessamad-27.jpg"
    )
)

enum class Language {
    ARABIC,
    ENGLISH
}

