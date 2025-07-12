package com.mostaqem.core.di.modules

import android.content.Context
import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.DownloadedAudioDao
import com.mostaqem.features.offline.domain.OfflineManager
import com.mostaqem.features.offline.domain.OfflineRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {
    @Provides
    @Singleton
    fun provideDownloadedSurahDao(database: AppDatabase): DownloadedAudioDao {
        return database.audioDao
    }

    @Provides
    @Singleton
    fun provideOfflineRepository(
        @ApplicationContext context: Context,
        manager: OfflineManager
    ): OfflineRepository {
        return OfflineRepository(context, manager)
    }

    @Provides
    @Singleton
    fun provideOfflineManager(
        @ApplicationContext context: Context,
        dao: DownloadedAudioDao
    ): OfflineManager {
        return OfflineManager(context, dao)
    }


}