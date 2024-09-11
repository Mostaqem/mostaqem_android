package com.mostaqem.screens.home.data

import com.google.gson.annotations.SerializedName

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