package com.mostaqem.features.player.domain.repository

import androidx.media3.common.MediaItem
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.favorites.data.FavoritedAudio
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.SurahAudio
import kotlinx.coroutines.flow.Flow

interface PlayerInterface {
    suspend fun getPlayer(): PlayerSurah?

    suspend fun savePlayer(player: PlayerSurah)

    suspend fun getMediaURL(
        surahID: Int,
        reciterID: Int,
        recitationID: Int?,
    ): Result<SurahAudio, DataError.Network>

    suspend fun buildQueue(currentSurahID: Int, reciterID: Int): List<MediaItem>

    fun download(audio: AudioData)

    fun getDownloadedProgress(surahID: Int, recitationID: Int): Flow<Float>

    fun getDownloadedMediaItem(surahID: Int, recitationID: Int): MediaItem?

    suspend fun getLocalMetadata(mediaItem: MediaItem): MediaItem?

    fun playURL()

    suspend fun addFavorite(data: AudioData)

    suspend fun isFavorited(data:AudioData) : Boolean

    suspend fun removeFavorite(data: FavoritedAudio)

}