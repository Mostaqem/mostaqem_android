package com.mostaqem.features.reciters.data.reciter

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Reciter(
    @PrimaryKey
    val id: Int,
    val image: String,
    @SerializedName("name_arabic")
    val arabicName: String,
    @SerializedName("name_english")
    val englishName: String,
    val lastAccessed: Long = System.currentTimeMillis()
)