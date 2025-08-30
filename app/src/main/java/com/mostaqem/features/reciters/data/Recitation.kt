package com.mostaqem.features.reciters.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mostaqem.features.reciters.data.reciter.Reciter
import kotlinx.serialization.Serializable

data class Recitation(
    @SerializedName("data")
    val response: List<RecitationData>,
    val status: Boolean
)

@Serializable
data class RecitationData(
    val id: Int,
    val name: String,
    @SerializedName("name_english")
    val englishName: String?,
    @SerializedName("reciter_id")
    val reciterID: Int,
    val reciter: Reciter?
)
