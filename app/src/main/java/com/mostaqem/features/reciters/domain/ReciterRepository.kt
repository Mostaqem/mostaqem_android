package com.mostaqem.features.reciters.domain

import androidx.paging.PagingData
import com.mostaqem.features.reciters.data.Recitation
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.data.reciter.ReciterResponse
import kotlinx.coroutines.flow.Flow

interface ReciterRepository {
    suspend fun getRemoteReciters(query: String?): Flow<PagingData<Reciter>>

    suspend fun getRemoteRecitations(reciterID: Int): Recitation

    suspend fun getReciters(query: String?): ReciterResponse

}