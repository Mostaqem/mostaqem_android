package com.mostaqem.features.player.data

import androidx.media3.common.MediaItem
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mostaqem.core.database.converter.Converters
import com.mostaqem.features.reciters.data.RecitationData
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
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
    val isLocal: Boolean = false
)


fun PlayerSurah.toAudioData(): AudioData {
    return AudioData(
        surah = this.surah!!.copy(complexName = this.surah.arabicName),
        recitationID = this.recitationID!!,
        url = this.url!!,
        size = 0,
        recitation = RecitationData(
            id = this.recitationID,
            reciter = this.reciter,
            name = "",
            reciterID = this.reciter.id,
            englishName = this.reciter.arabicName
        )
    )
}