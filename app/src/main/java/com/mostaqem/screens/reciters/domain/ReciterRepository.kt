package com.mostaqem.screens.reciters.domain

import androidx.paging.PagingData
import com.mostaqem.screens.reciters.data.reciter.Reciter
import kotlinx.coroutines.flow.Flow

interface ReciterRepository {
    suspend fun getRemoteReciters(): Flow<PagingData<Reciter>>
}