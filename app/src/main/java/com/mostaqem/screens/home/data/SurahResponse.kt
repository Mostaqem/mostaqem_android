package com.mostaqem.screens.home.data

import com.google.gson.annotations.SerializedName

data class SurahResponse(
    @SerializedName("data")
    val response: List<Surah>,
)
