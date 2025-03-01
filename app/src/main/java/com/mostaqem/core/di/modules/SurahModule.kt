package com.mostaqem.core.di.modules

import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.features.surahs.data.SurahRepositoryImpl
import com.mostaqem.features.surahs.domain.repository.SurahRepository
import com.mostaqem.features.surahs.domain.repository.SurahService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SurahModule {

    @Provides
    @Singleton
    fun provideSurahService(
        retrofit: Retrofit
    ): SurahService {
        return retrofit.create(SurahService::class.java)
    }

    @Provides
    @Singleton
    fun provideSurahRepository(api: SurahService): SurahRepository {
        return SurahRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSurahDao(database: AppDatabase): SurahDao {
        return database.surahDao
    }
}