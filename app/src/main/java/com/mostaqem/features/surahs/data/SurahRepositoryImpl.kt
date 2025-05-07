package com.mostaqem.features.surahs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
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
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { SurahPagingSource(api = api) }).flow
    }


    override suspend fun getSurahUrl(
        surahID: Int,
        reciterID: Int,
        recitationID: Int?
    ): Result<SurahAudio, DataError.Network> {
//        return response.body()!!;
        return safeApiCall(
            apiCall = {
                api.getAudio(
                    surahID = surahID,
                    reciterID = reciterID,
                    recitationID = recitationID
                )
            },
            mapper = { it })
    }

    override suspend fun getSurahs(query: String?): SurahResponse {
        return api.getQuerySurah(query).body()!!
    }

    override suspend fun getRandomSurahs(
        limit: Int, reciterID: Int?
    ): Result<List<AudioData>, DataError.Network> {
        return safeApiCall(
            apiCall = { api.getRandomSurahs(limit, reciterID) },
            mapper = { it.response })
    }
}