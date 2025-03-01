package com.mostaqem.features.surahs.data

import com.google.gson.annotations.SerializedName
import com.mostaqem.features.reciters.data.RecitationData

data class SurahAudio(
    @SerializedName("data")
    val response: AudioData,
    val status: Boolean
)

data class AudioData(
    val surah: Surah,
    @SerializedName("tilawa")
    val recitation: RecitationData,
    @SerializedName("tilawa_id")
    val recitationID: Int,
    val url: String,
    val size: Long = 0
)

data class RandomSurahAudio(
    @SerializedName("data")
    val response: List<AudioData>,
    val status: Boolean
)