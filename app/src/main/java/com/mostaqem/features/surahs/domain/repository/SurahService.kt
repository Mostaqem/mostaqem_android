package com.mostaqem.features.surahs.domain.repository

import com.mostaqem.features.surahs.data.RandomSurahAudio
import com.mostaqem.features.surahs.data.SurahAudio
import com.mostaqem.features.surahs.data.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SurahService {
    @GET("surah?take=10")
    suspend fun getSurahs(@Query("page") page: Int): Response<SurahResponse>

    @GET("audio")
    suspend fun getAudio(
        @Query("surah_id") surahID: Int,
        @Query("reciter_id") reciterID: Int,
        @Query("tilawa_id") recitationID: Int?
    ): Response<SurahAudio>

    @GET("surah?take=10&page=1")
    suspend fun getQuerySurah(@Query("name") query: String?): Response<SurahResponse>

    @GET("audio/random")
    suspend fun getRandomSurahs(
        @Query("limit") limit: Int,
        @Query("reciter_id") reciterID: Int? = null
    ): Response<RandomSurahAudio>

}