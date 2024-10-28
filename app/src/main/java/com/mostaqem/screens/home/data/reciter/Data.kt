package com.mostaqem.screens.home.data.reciter

import com.google.gson.annotations.SerializedName

data class Reciter(
    val id: Int,
    val image: String,
    @SerializedName("name_arabic")
    val arabicName: String,
    @SerializedName("name_english")
    val englishName: String
)