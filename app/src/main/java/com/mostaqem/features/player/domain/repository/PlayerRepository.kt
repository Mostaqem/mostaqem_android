package com.mostaqem.features.player.domain.repository

import androidx.media3.common.MediaItem
import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.SurahAudio
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val dao: PlayerDao,
    private val surahRepository: SurahRepository
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
        return surahRepository.getSurahUrl(surahID,reciterID,recitationID)
    }

    override suspend fun buildQueue(
        currentSurahID: Int,
        reciterID: Int
    ): List<MediaItem> {
        TODO("Not yet implemented")
    }


}