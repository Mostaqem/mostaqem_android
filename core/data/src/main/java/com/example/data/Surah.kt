package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


data class SurahResponse(
    @SerializedName("data") val response: SurahObject,
)

data class SurahObject(
    @SerializedName("surah") val surahData: List<Surah>
)

@Serializable
@Entity
data class Surah(
    @PrimaryKey val id: Int,
    val image: String,
    @SerializedName("name_arabic") val arabicName: String,

    @SerializedName("name_complex") val complexName: String,

    @SerializedName("revelation_place") val revelationPlace: String,

    @SerializedName("verses_count") val versusCount: Int,

    val lastAccessed: Long = System.currentTimeMillis()
)

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
