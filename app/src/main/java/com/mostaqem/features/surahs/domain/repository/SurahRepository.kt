package com.mostaqem.features.surahs.domain.repository

import androidx.paging.PagingData
import com.mostaqem.core.network.models.NetworkResult
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.RandomSurahAudio
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.data.SurahAudio
import com.mostaqem.features.surahs.data.SurahResponse
import kotlinx.coroutines.flow.Flow

interface SurahRepository {
    suspend fun getRemoteSurahs(): Flow<PagingData<Surah>>

    suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio

    suspend fun getSurahs(query: String?) : SurahResponse

    suspend fun getRandomSurahs(limit: Int, reciterID: Int? = null) : NetworkResult<List<AudioData>>
}