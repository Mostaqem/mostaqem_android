package com.mostaqem.screens.home.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mostaqem.screens.home.domain.HomeService
import com.mostaqem.screens.home.data.SurahResponse
import com.mostaqem.screens.home.data.Surah
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeViewModel() : ViewModel() {
    private var homeInterface: HomeService
    val state = mutableStateOf<List<Surah>>(emptyList<Surah>())
    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    init {
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://mostaqem-api.onrender.com/api/v1/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).build()

        homeInterface = retrofit.create(
            HomeService::class.java
        )
        viewModelScope.launch(errorHandler) {
            val surahs = getRemoteSurahs()
            state.value = surahs.response
        }

    }


    private suspend fun getRemoteSurahs(): SurahResponse {

        return homeInterface.getSurahs()


    }


}