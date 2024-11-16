package com.mostaqem.screens.reciters.domain

import com.mostaqem.screens.reciters.data.reciter.ReciterResponse
import com.mostaqem.screens.surahs.data.SurahResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReciterService{
    @GET("reciter?take=5")
    suspend fun getReciters(@Query("page") page: Int): ReciterResponse
}