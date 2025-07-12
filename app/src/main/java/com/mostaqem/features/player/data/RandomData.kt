package com.mostaqem.features.player.data

import com.google.gson.annotations.SerializedName
import com.mostaqem.features.surahs.data.AudioData

data class RandomSurahAudio(
    @SerializedName("data")
    val response: List<AudioData>,
    val status: Boolean
)