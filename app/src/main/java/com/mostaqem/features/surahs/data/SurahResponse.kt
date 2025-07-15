package com.mostaqem.features.surahs.data

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
    val image: String = "https://img.freepik.com/premium-photo/illustration-mosque-with-crescent-moon-stars-simple-shapes-minimalist-flat-design_217051-15556.jpg",
    @SerializedName("name_arabic") val arabicName: String,

    @SerializedName("name_complex") val complexName: String,

    @SerializedName("revelation_place") val revelationPlace: String,

    @SerializedName("verses_count") val versusCount: Int,

    val lastAccessed: Long = System.currentTimeMillis(),

)

enum class SurahSortBy {
    ID,
    NAME,
    REVELATION_PLACE
}

data class SortItems(val name: String, val value: SurahSortBy)
