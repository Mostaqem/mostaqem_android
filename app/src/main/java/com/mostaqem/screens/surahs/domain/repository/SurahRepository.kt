package com.mostaqem.screens.surahs.domain.repository

import androidx.paging.PagingData
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.data.SurahAudio
import com.mostaqem.screens.surahs.data.SurahResponse
import kotlinx.coroutines.flow.Flow

interface SurahRepository {
    suspend fun getRemoteSurahs(): Flow<PagingData<Surah>>

    suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio

    suspend fun getSurahs(query: String?) : SurahResponse
}