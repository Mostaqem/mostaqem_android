package com.mostaqem.features.surahs.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mostaqem.core.network.models.DataError
import com.mostaqem.core.network.models.Result
import com.mostaqem.core.network.safeApiCall
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.surahs.domain.repository.SurahPagingSource
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import com.mostaqem.features.surahs.domain.repository.SurahService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Singleton

@Singleton
class SurahRepositoryImpl(
    private val api: SurahService,
    private val personalizationRepository: PersonalizationRepository
) : SurahRepository {
    override suspend fun getRemoteSurahs(sortBy: SurahSortBy): Flow<PagingData<Surah>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { SurahPagingSource(api = api, defaultSortBy = sortBy) }).flow
    }


    override suspend fun getSurahUrl(
        surahID: Int,
        reciterID: Int,
        recitationID: Int?
    ): Result<SurahAudio, DataError.Network> {
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

    override suspend fun getAllSurahsUrls(

    ): Result<List<AudioData>, DataError.Network> {
        val defaultReciter = personalizationRepository.getDefaultReciter().first()
        val defaultRecitationID = personalizationRepository.getDefaultRecitation().first().id
        return coroutineScope {
            val deferredResults = (1..114).map { surahId ->
                async {
                    getSurahUrl(
                        surahID = surahId,
                        reciterID = defaultReciter.id,
                        recitationID = defaultRecitationID
                    )
                }
            }

            val results = deferredResults.awaitAll()
            val audios = mutableListOf<AudioData>()
            for (result in results) {
                when (result) {
                    is Result.Success -> {
                        audios.add(result.data.response)
                    }

                    is Result.Error -> {
                        return@coroutineScope Result.Error(result.error)
                    }

                    else -> null
                }
            }
            Result.Success(audios)
        }
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