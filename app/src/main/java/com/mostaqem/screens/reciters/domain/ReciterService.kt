package com.mostaqem.screens.reciters.domain

import com.mostaqem.screens.reciters.data.Recitation
import com.mostaqem.screens.reciters.data.reciter.ReciterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReciterService {
    @GET("reciter?take=20")
    suspend fun getReciters(@Query("page") page: Int, @Query("name") query: String?): ReciterResponse

    @GET("reciter/{reciter_id}/tilawa")
    suspend fun getRecitations(@Path("reciter_id") reciterID: Int): Recitation

}