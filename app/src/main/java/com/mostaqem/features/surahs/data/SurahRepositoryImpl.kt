package com.mostaqem.features.surahs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mostaqem.core.network.models.NetworkResult
import com.mostaqem.core.network.safeApiCall
import com.mostaqem.features.surahs.domain.repository.SurahPagingSource
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import com.mostaqem.features.surahs.domain.repository.SurahService
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class SurahRepositoryImpl(
    private val api: SurahService,
) : SurahRepository {
    override suspend fun getRemoteSurahs(): Flow<PagingData<Surah>> {
        return Pager(config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,
            initialLoadSize = 5
        ),
            pagingSourceFactory = { SurahPagingSource(api = api) }).flow
    }


    override suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio {
        val response =
            api.getAudio(surahID = surahID, reciterID = reciterID, recitationID = recitationID)
        return response.body()!!;
    }

    override suspend fun getSurahs(query: String?): SurahResponse {
        return api.getQuerySurah(query).body()!!
    }

    override suspend fun getRandomSurahs(
        limit: Int, reciterID: Int?
    ): NetworkResult<List<AudioData>> {
        return safeApiCall(apiCall = { api.getRandomSurahs(limit, reciterID) },
            mapper = { it.response })
    }
}