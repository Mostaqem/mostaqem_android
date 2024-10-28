package com.mostaqem.screens.home.data.repository

import com.mostaqem.screens.home.data.reciter.ReciterResponse
import com.mostaqem.screens.home.data.surah.SurahAudio
import com.mostaqem.screens.home.data.surah.SurahResponse
import com.mostaqem.screens.home.domain.HomeService
import com.mostaqem.screens.home.domain.repository.HomeRepository
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl(
    private val api: HomeService
) : HomeRepository {


    override suspend fun getRemoteSurahs(): SurahResponse {
        return api.getSurahs(page = 1, take = 20)

    }

    override suspend fun getRemoteReciters(): ReciterResponse {
        return api.getReciters(page = 1, take = 10)
    }

    override suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio {
        return api.getAudio(surahID = surahID, reciterID = reciterID, recitationID = recitationID)
    }
}