package com.mostaqem.screens.surahs.domain.repository

import com.mostaqem.screens.surahs.data.SurahAudio
import com.mostaqem.screens.surahs.data.SurahResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SurahService {
    @GET("surah?take=10")
    suspend fun getSurahs(@Query("page") page: Int): SurahResponse

    @GET("audio")
    suspend fun getAudio(
        @Query("surah_id") surahID: Int,
        @Query("reciter_id") reciterID: Int,
        @Query("tilawa_id") recitationID: Int?
    ): SurahAudio
}