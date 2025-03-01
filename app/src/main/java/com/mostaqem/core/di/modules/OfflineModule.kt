package com.mostaqem.core.di.modules

import android.content.Context
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
    fun provideOfflineRepository(@ApplicationContext context: Context): OfflineRepository {
        return OfflineRepository(context)
    }
}