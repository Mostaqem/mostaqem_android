package com.mostaqem.screens.home.domain.repository

import com.mostaqem.screens.home.data.reciter.ReciterResponse
import com.mostaqem.screens.home.data.surah.SurahAudio
import com.mostaqem.screens.home.data.surah.SurahResponse

interface HomeRepository {
    suspend fun getRemoteSurahs(): SurahResponse

    suspend fun getRemoteReciters(): ReciterResponse

    suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio

}