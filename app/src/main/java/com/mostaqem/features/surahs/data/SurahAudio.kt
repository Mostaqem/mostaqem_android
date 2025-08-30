package com.mostaqem.features.surahs.data

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mostaqem.features.favorites.data.FavoritedAudio
import com.mostaqem.features.reciters.data.RecitationData

data class SurahAudio(
    @SerializedName("data")
    val response: AudioData,
    val status: Boolean
)


data class AudioData(
    @PrimaryKey val id: Int,
    val surah: Surah,
    @SerializedName("tilawa")
    val recitation: RecitationData,
    @SerializedName("tilawa_id")
    val recitationID: Int,
    val url: String,
    val size: Long = 0,
)


fun AudioData.toFavoritedAudio(): FavoritedAudio {
    return FavoritedAudio(
        surahID = this.surah.id.toString(),
        reciter = this.recitation.reciter?.arabicName ?: "",
        reciterID = this.recitation.reciter?.id.toString(),
        surahName = this.surah.arabicName,
        recitationID = this.recitationID.toString(),
        remoteUrl = this.url,
        id = "${this.surah.id}-${this.recitationID}"
    )

}