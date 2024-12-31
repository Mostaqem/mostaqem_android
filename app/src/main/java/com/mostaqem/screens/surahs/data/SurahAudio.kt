package com.mostaqem.screens.surahs.data

import com.google.gson.annotations.SerializedName
import com.mostaqem.screens.reciters.data.RecitationData
import com.mostaqem.screens.reciters.data.reciter.Reciter

data class SurahAudio(
    @SerializedName("data")
    val response: Data,
    val status: Boolean
)

data class Data(
    val surah: Surah,
    @SerializedName("tilawa")
    val recitation: RecitationData,
    @SerializedName("tilawa_id")
    val recitationID: Int,
    val url: String
)
