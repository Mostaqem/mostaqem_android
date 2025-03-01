package com.mostaqem.features.settings.data

import com.mostaqem.features.reciters.data.reciter.Reciter
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val shapeID: String = "rect",
    val language: Language = Language.ARABIC,
    val reciterSaved: Reciter = Reciter(
        id = 1,
        arabicName = "عبدالباسط عبدالصمد",
        englishName = "AbdulBaset AbdulSamad",
        image = "https://upload.wikimedia.org/wikipedia/commons/5/55/Abdelbasset-abdessamad-27.jpg",
    ),
    val recitationID: Int = 178,
    val playDownloaded: Boolean = true,

)

enum class Language {
    ARABIC,
    ENGLISH
}

