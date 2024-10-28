package com.mostaqem.screens.home.data.reciter

import com.google.gson.annotations.SerializedName

data class ReciterResponse(
    @SerializedName("data")
    val response: ReciterObject,
)

data class ReciterObject(
    val reciters: List<Reciter>
)