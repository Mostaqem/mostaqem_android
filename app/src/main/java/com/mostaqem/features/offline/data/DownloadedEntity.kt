package com.mostaqem.features.offline.data

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mostaqem.features.surahs.data.AudioData
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "downloaded_audios")
data class DownloadedAudioEntity(
    @PrimaryKey val id: String,
    val title: String,
    val size: Long = 0,
    val reciter: String,
    val reciterID: String,
    val recitationID : String,
    val surahID: String,
    val remoteUrl: String,
)


@UnstableApi
data class DownloadUiState(
    val downloadID : String,
    val progress: Float = 0f,
    val state: Int = Download.STATE_QUEUED,
    val downloadAudio: AudioData? = null
)
