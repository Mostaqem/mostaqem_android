package com.mostaqem.features.reciters.data

import com.google.gson.annotations.SerializedName
import com.mostaqem.features.reciters.data.reciter.Reciter

data class Recitation(
    @SerializedName("data")
    val response: List<RecitationData>,
    val status: Boolean
)

data class RecitationData(
    val id: Int,
    val name: String,
    @SerializedName("name_english")
    val englishName: String?,
    @SerializedName("reciter_id")
    val reciterID: Int,
    val reciter: Reciter
)
