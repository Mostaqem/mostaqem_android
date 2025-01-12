package com.example.network

import com.example.data.Recitation
import com.example.data.ReciterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ReciterService {
    @GET("reciter?take=20")
    suspend fun getReciters(@Query("page") page: Int, @Query("name") query: String?): ReciterResponse

    @GET("reciter/{reciter_id}/tilawa")
    suspend fun getRecitations(@Path("reciter_id") reciterID: Int): Recitation

    @GET("reciter?take=10&page=1")
    suspend fun getQueryReciters(@Query("name") query: String?): ReciterResponse
}