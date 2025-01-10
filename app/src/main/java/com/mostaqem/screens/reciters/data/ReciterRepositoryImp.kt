package com.mostaqem.screens.reciters.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.data.reciter.ReciterResponse
import com.mostaqem.screens.reciters.domain.ReciterPagingSource
import com.mostaqem.screens.reciters.domain.ReciterRepository
import com.mostaqem.screens.reciters.domain.ReciterService
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class ReciterRepositoryImp(private val api: ReciterService) : ReciterRepository {
    override suspend fun getRemoteReciters(query: String?): Flow<PagingData<Reciter>> {
        return Pager(config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { ReciterPagingSource(api = api, query = query) }).flow
    }

    override suspend fun getRemoteRecitations(reciterID: Int): Recitation {
        return api.getRecitations(reciterID)
    }

    override suspend fun getReciters(query: String?): ReciterResponse {
        return api.getQueryReciters(query)
    }

}