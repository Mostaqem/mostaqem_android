package com.mostaqem.features.player.domain.repository

import androidx.media3.common.MediaItem
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.surahs.data.SurahAudio

interface PlayerInterface {
    suspend fun getPlayer(): PlayerSurah?

    suspend fun savePlayer(player: PlayerSurah)

    suspend fun getMediaURL(
        surahID: Int,
        reciterID: Int,
        recitationID: Int?,
    ): Result<SurahAudio, DataError.Network>

    suspend fun buildQueue(currentSurahID: Int, reciterID: Int): List<MediaItem>


}