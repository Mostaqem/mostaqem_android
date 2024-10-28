package com.mostaqem.screens.home.data.surah

import com.google.gson.annotations.SerializedName

data class SurahAudio(
    @SerializedName("data")
    val response: Data,
    val status: Boolean
)

data class Data(
    @SerializedName("reciter_id")
    val reciterID: Int,
    @SerializedName("surah_id")
    val surahID: Int,
    @SerializedName("tilawa_id")
    val recitationID: Int,
    val url: String
)