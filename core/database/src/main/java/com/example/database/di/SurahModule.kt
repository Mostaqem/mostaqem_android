package com.example.database.di

import com.example.database.AppDatabase
import com.example.database.dao.SurahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SurahModule {
    @Provides
    @Singleton
    fun provideSurahDao(database: AppDatabase): SurahDao {
        return database.surahDao
    }
}