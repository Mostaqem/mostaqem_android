package com.mostaqem.features.settings.data

import com.mostaqem.features.language.domain.AppLanguages
import com.mostaqem.features.reciters.data.reciter.Reciter
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val shapeID: String = "rect",
    val language: AppLanguages = AppLanguages.ENGLISH,
    val reciterSaved: Reciter = Reciter(
        id = 1,
        arabicName = "عبدالباسط عبدالصمد",
        englishName = "AbdulBaset AbdulSamad",
        image = "https://upload.wikimedia.org/wikipedia/ar/7/73/%D8%B5%D9%88%D8%B1%D8%A9_%D8%B4%D8%AE%D8%B5%D9%8A%D8%A9_%D8%B9%D8%A8%D8%AF_%D8%A7%D9%84%D8%A8%D8%A7%D8%B3%D8%B7_%D8%B9%D8%A8%D8%AF_%D8%A7%D9%84%D8%B5%D9%85%D8%AF.png",
    ),
    val recitationID: Int = 178,
    val playDownloaded: Boolean = true,

    )


