package com.mostaqem.screens.reciters.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.domain.ReciterPagingSource
import com.mostaqem.screens.reciters.domain.ReciterRepository
import com.mostaqem.screens.reciters.domain.ReciterService
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class ReciterRepositoryImp(private val api: ReciterService) : ReciterRepository {
    override suspend fun getRemoteReciters(): Flow<PagingData<Reciter>> {
        return Pager(config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { ReciterPagingSource(api) }).flow
    }

}