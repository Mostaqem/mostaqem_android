package com.example.network.di

import android.content.Context
import com.example.network.ReciterService
import com.example.network.SurahService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize.toLong())
        return OkHttpClient.Builder().cache(cache).addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://mostaqem-api.onrender.com/api/v1/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                ).client(okHttpClient).build()
        return retrofit;
    }

    @Provides
    @Singleton
    fun provideSurahService(
        retrofit: Retrofit
    ): SurahService {
        return retrofit.create(SurahService::class.java)
    }

    @Provides
    @Singleton
    fun provideReciterService(
        retrofit: Retrofit
    ): ReciterService {
        return retrofit.create(ReciterService::class.java)
    }

}