package com.mostaqem.core.di.modules

import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.screens.reciters.data.ReciterRepositoryImp
import com.mostaqem.screens.reciters.domain.ReciterRepository
import com.mostaqem.screens.reciters.domain.ReciterService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReciterModule {
    @Provides
    @Singleton
    fun provideReciterService(
        retrofit: Retrofit
    ): ReciterService {
        return retrofit.create(ReciterService::class.java)
    }

    @Provides
    @Singleton
    fun provideReciterRepository(api: ReciterService): ReciterRepository {
        return ReciterRepositoryImp(api)
    }

    @Provides
    @Singleton
    fun provideReciterDao(database: AppDatabase): ReciterDao {
        return database.reciterDao
    }

}