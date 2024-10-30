package com.mostaqem.screens.home.domain

import com.mostaqem.screens.home.data.reciter.ReciterResponse
import com.mostaqem.screens.home.data.surah.SurahAudio
import com.mostaqem.screens.home.data.surah.SurahResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface HomeService {

    @GET("surah")
    suspend fun getSurahs(@Query("page") page: Int, @Query("take") take: Int): SurahResponse

    @GET("reciter")
    suspend fun getReciters(@Query("page") page: Int, @Query("take") take: Int): ReciterResponse

    @GET("audio")
    suspend fun getAudio(
        @Query("surah_id") surahID: Int,
        @Query("reciter_id") reciterID: Int,
        @Query("tilawa_id") recitationID: Int?
    ): SurahAudio

}