package com.mostaqem.screens.reciters.domain

import androidx.paging.PagingData
import com.mostaqem.screens.reciters.data.Recitation
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.data.reciter.ReciterResponse
import kotlinx.coroutines.flow.Flow

interface ReciterRepository {
    suspend fun getRemoteReciters(query: String?): Flow<PagingData<Reciter>>

    suspend fun getRemoteRecitations(reciterID: Int): Recitation

    suspend fun getReciters(query: String?): ReciterResponse

    suspend fun getDefaultReciter(): Flow<Reciter>?

    suspend fun saveDefaultReciter(reciter: Reciter)

}