package com.mostaqem.screens.home.domain
import com.mostaqem.screens.home.data.SurahResponse
import retrofit2.http.GET


interface HomeService {

    @GET("surah")
    suspend fun getSurahs(): SurahResponse

}