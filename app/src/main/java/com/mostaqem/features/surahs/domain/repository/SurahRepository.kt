package com.mostaqem.features.surahs.domain.repository

import androidx.paging.PagingData
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.data.SurahAudio
import com.mostaqem.features.surahs.data.SurahResponse
import com.mostaqem.features.surahs.data.SurahSortBy
import kotlinx.coroutines.flow.Flow

interface SurahRepository {
    suspend fun getRemoteSurahs(sortBy: SurahSortBy): Flow<PagingData<Surah>>

    suspend fun getSurahUrl(
        surahID: Int,
        reciterID: Int,
        recitationID: Int?
    ): Result<SurahAudio, DataError.Network>

    suspend fun getSurahs(query: String?): SurahResponse

    suspend fun getRandomSurahs(
        limit: Int,
        reciterID: Int? = null
    ): Result<List<AudioData>, DataError.Network>
}