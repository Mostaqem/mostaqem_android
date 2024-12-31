package com.mostaqem.core.di.modules

import android.content.Context
import androidx.room.Room
import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.screens.home.domain.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, name = "database.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHomeRepository(surahDao: SurahDao, reciterDao: ReciterDao): HomeRepository {
        return HomeRepository(surahDao = surahDao, reciterDao = reciterDao)
    }


}
