package com.mostaqem.features.reciters.domain

import com.mostaqem.features.reciters.data.Recitation
import com.mostaqem.features.reciters.data.reciter.ReciterResponse
import com.mostaqem.features.surahs.data.SurahResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReciterService {
    @GET("reciter?take=12")
    suspend fun getReciters(@Query("page") page: Int, @Query("name") query: String?): ReciterResponse

    @GET("reciter/{reciter_id}/tilawa")
    suspend fun getRecitations(@Path("reciter_id") reciterID: Int): Recitation

    @GET("reciter/search")
    suspend fun getQueryReciters(@Query("name") query: String?): ReciterResponse
}