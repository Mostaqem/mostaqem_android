package com.mostaqem.features.player.domain.repository

import android.os.CountDownTimer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.mostaqem.core.database.dao.DownloadedAudioDao
import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.favorites.data.FavoritedAudio
import com.mostaqem.features.favorites.domain.FavoritesRepository
import com.mostaqem.features.offline.domain.OfflineManager
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.SurahAudio
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val dao: PlayerDao,
    private val surahRepository: SurahRepository,
    private val offlineManager: OfflineManager,
    private val offlineDao: DownloadedAudioDao,
    private val favorites: FavoritesRepository

) : PlayerInterface {


    override suspend fun getPlayer(): PlayerSurah? {
        return dao.getPlayer()
    }

    override suspend fun savePlayer(player: PlayerSurah) {
        dao.savePlayer(player)
    }

    override suspend fun getMediaURL(
        surahID: Int,
        reciterID: Int,
        recitationID: Int?,
    ): Result<SurahAudio, DataError.Network> {
        return surahRepository.getSurahUrl(surahID, reciterID, recitationID)
    }

    override suspend fun buildQueue(
        currentSurahID: Int,
        reciterID: Int
    ): List<MediaItem> {
        TODO("Not yet implemented")
    }

    override fun download(audio: AudioData) {
        offlineManager.startDownload(audio)
    }

    override fun getDownloadedProgress(surahID: Int, recitationID: Int): Flow<Float> {
        return offlineManager.getDownloadProgress(surahID, recitationID)
    }

    override fun getDownloadedMediaItem(surahID: Int, recitationID: Int): MediaItem? {
        return offlineManager.getDownloadedMediaItem(surahID, recitationID)
    }

    override suspend fun getLocalMetadata(mediaItem: MediaItem): MediaItem? {
        val data = offlineDao.getDownloadedAudio(mediaItem.mediaId)
        if (data != null) {
            val metadata =
                MediaMetadata.Builder().setTitle(data.title).setAlbumTitle(data.reciterID)
                    .setArtist(data.reciter).setAlbumArtist(data.recitationID).build()
            return mediaItem.buildUpon().setMediaId(data.surahID).setMediaMetadata(metadata).build()
        }
        return null

    }

    override fun playURL() {

    }

    override suspend fun addFavorite(data: AudioData) {
        favorites.favorite(data)
    }

    override suspend fun isFavorited(data: AudioData): Boolean {
        return favorites.isFavorited(
            surahID = data.surah?.id.toString(),
            recitationID = data.recitationID.toString()
        )
    }

    override suspend fun removeFavorite(data: FavoritedAudio) {
        favorites.removeFavorite(data)
    }


}