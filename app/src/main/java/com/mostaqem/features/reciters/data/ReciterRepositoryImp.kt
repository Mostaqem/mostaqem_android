package com.mostaqem.features.reciters.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mostaqem.dataStore
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.data.reciter.ReciterResponse
import com.mostaqem.features.reciters.domain.ReciterPagingSource
import com.mostaqem.features.reciters.domain.ReciterRepository
import com.mostaqem.features.reciters.domain.ReciterService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReciterRepositoryImp @Inject constructor(
    private val api: ReciterService,
) : ReciterRepository {
    override suspend fun getRemoteReciters(query: String?): Flow<PagingData<Reciter>> {
        return Pager(config = PagingConfig(
            pageSize = 12,
            initialLoadSize = 6,
            prefetchDistance = 3
        ),
            pagingSourceFactory = { ReciterPagingSource(api = api, query = query) }).flow
    }

    override suspend fun getRemoteRecitations(reciterID: Int): Recitation {
        return api.getRecitations(reciterID)
    }

    override suspend fun getReciters(query: String?): ReciterResponse {
        return api.getQueryReciters(query)
    }

}