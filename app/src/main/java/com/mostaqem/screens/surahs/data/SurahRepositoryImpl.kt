package com.mostaqem.screens.surahs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.mostaqem.screens.surahs.domain.repository.SurahPagingSource
import com.mostaqem.screens.surahs.domain.repository.SurahRepository
import com.mostaqem.screens.surahs.domain.repository.SurahService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Singleton

@Singleton
class SurahRepositoryImpl(
    private val api: SurahService
) : SurahRepository {
    override suspend fun getRemoteSurahs(): Flow<PagingData<Surah>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {SurahPagingSource(api)}
        ).flow
    }


    override suspend fun getSurahUrl(surahID: Int, reciterID: Int, recitationID: Int?): SurahAudio {
        return api.getAudio(surahID = surahID, reciterID = reciterID, recitationID = recitationID)
    }



}