package com.mostaqem.screens.surahs.data

import com.google.gson.annotations.SerializedName
import com.mostaqem.screens.reciters.data.reciter.Reciter

data class SurahAudio(
    @SerializedName("data")
    val response: Data,
    val status: Boolean
)

data class Data(
    val surah: Surah,
    @SerializedName("tilawa")
    val recitation: Recitation,
    @SerializedName("tilawa_id")
    val recitationID: Int,
    val url: String
)

data class Recitation(
    val id: Int,
    val name:String,
    @SerializedName("name_english")
    val englishName: String,
    @SerializedName("reciter_id")
    val reciterID: Int,
    val reciter: Reciter
)