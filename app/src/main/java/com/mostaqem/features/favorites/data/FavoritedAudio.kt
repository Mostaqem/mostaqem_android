package com.mostaqem.features.favorites.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

@Serializable
@Entity(tableName = "favorited_audio")
data class FavoritedAudio(
    @PrimaryKey val id: String,
    val surahName: String? = null,
    val reciter: String,
    val reciterID: String,
    val recitationID: String,
    val surahID: String,
    val remoteUrl: String,
)

fun FavoritedAudio.toMediaItem(): MediaItem {
    val metadata =
        MediaMetadata.Builder().setTitle(this.surahName).setAlbumTitle(reciterID).setArtist(reciter)
            .setAlbumArtist(recitationID).build()
    val mediaItem =
        MediaItem.Builder().setMediaId(this.surahID).setMediaMetadata(metadata)
            .setUri(this.remoteUrl)
            .build()
    return mediaItem
}