package com.mostaqem.screens.home.data.surah

import com.google.gson.annotations.SerializedName

data class SurahResponse(
    @SerializedName("data")
    val response: SurahObject,
)

data class SurahObject(
    @SerializedName("surah")
    val surahData: List<Surah>
)

data class Surah(
    val id: Int,
    val image: String,
    @SerializedName("name_arabic")
    val arabicName: String,

    @SerializedName("name_complex")
    val complexName: String,

    @SerializedName("revelation_place")
    val revelationPlace: String,

    @SerializedName("verses_count")
    val versusCount: Int
)