package com.mostaqem.core.di.modules

import android.content.Context
import androidx.room.Room
import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.ignoreAllSSLErrors
import com.mostaqem.dataStore
import com.mostaqem.features.history.domain.HomeRepository
import com.mostaqem.features.settings.data.AppSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
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
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor, @ApplicationContext context: Context
    ): OkHttpClient {
        val cacheSize = 10 * 1024 * 1024
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize.toLong())
        return OkHttpClient.Builder().cache(cache).ignoreAllSSLErrors()
            .addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://209.38.241.76/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.newBuilder()
                .build())
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, name = "database.db"
        ).addMigrations(AppDatabase.migrate2To3).build()
    }

    @Provides
    @Singleton
    fun provideHomeRepository(surahDao: SurahDao, reciterDao: ReciterDao): HomeRepository {
        return HomeRepository(surahDao = surahDao, reciterDao = reciterDao)
    }


    @Provides
    @Singleton
    fun provideNetworkObserver(@ApplicationContext context: Context): NetworkConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }


    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext context: Context): Flow<AppSettings> {
        return context.dataStore.data
    }

}


