package com.mostaqem.screens.surahs.domain.repository

import androidx.paging.PagingData
import com.mostaqem.core.network.models.NetworkResult
import com.mostaqem.screens.surahs.data.AudioData
import com.mostaqem.screens.surahs.data.RandomSurahAudio
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.surahs.data.SurahAudio
import com.mostaqem.screens.surahs.data.SurahResponse
import kotlinx.coroutines.flow.Flow

interface SurahRepository {
    suspend fun getRemoteSurahs(): Flow<PagingData<Surah>>

    suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio

    suspend fun getSurahs(query: String?) : SurahResponse

    suspend fun getRandomSurahs(limit: Int, reciterID: Int? = null) : NetworkResult<List<AudioData>>
}