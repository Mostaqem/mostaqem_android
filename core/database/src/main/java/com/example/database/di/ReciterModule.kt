package com.example.database.di

import com.example.database.AppDatabase
import com.example.database.dao.ReciterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReciterModule {
    @Provides
    @Singleton
    fun provideReciterDao(database: AppDatabase): ReciterDao {
        return database.reciterDao
    }
}