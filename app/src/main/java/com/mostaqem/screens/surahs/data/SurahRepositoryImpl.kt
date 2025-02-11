package com.mostaqem.screens.surahs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.mostaqem.core.network.models.NetworkResult
import com.mostaqem.core.network.safeApiCall
import com.mostaqem.screens.surahs.domain.repository.SurahPagingSource
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import com.mostaqem.screens.surahs.domain.repository.SurahService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Singleton

@Singleton
class SurahRepositoryImpl(
    private val api: SurahService,
) : SurahRepository {
    override suspend fun getRemoteSurahs(): Flow<PagingData<Surah>> {
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 5, initialLoadSize = 5),
            pagingSourceFactory = { SurahPagingSource(api = api) }
        ).flow
    }


    override suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio {
//        return try{
//            val response = api.getAudio(surahID = surahID, reciterID = reciterID, recitationID = recitationID)
//            if (response.isSuccessful){
//                NetworkResult.Success(response.body()!!.response)
//
//            }
//        }
        val response =
            api.getAudio(surahID = surahID, reciterID = reciterID, recitationID = recitationID)

        return response.body()!!;


//        return api.getAudio(surahID = surahID, reciterID = reciterID, recitationID = recitationID)
    }

    override suspend fun getSurahs(query: String?): SurahResponse {
        return api.getQuerySurah(query).body()!!
    }

    override suspend fun getRandomSurahs(
        limit: Int,
        reciterID: Int?
    ): NetworkResult<List<AudioData>> {
        return safeApiCall(
            apiCall = { api.getRandomSurahs(limit, reciterID) },
            mapper = { it.response }
        )
    }


}