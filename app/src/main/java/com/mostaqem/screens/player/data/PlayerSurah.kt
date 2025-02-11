package com.mostaqem.screens.player.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mostaqem.core.database.converter.Converters
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Surah
import kotlinx.serialization.Serializable



@Entity
@TypeConverters(Converters::class)
@Serializable
data class PlayerSurah(
    @PrimaryKey(autoGenerate = false) val id: Int = 1,
    val surah: Surah? = null,
    val reciter: Reciter,
    val url: String? = null,
    val recitationID: Int? = null,
    val progress: Float = 0f,
    val position: Long = 0L,
)

