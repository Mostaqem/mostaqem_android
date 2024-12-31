package com.mostaqem.screens.settings.data

import com.google.gson.annotations.SerializedName

data class Release(
    @SerializedName("tag_name")
    val tagName: String,
    val assets: List<Asset>
)

data class Asset(
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String
)