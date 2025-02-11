package com.mostaqem.core.di.modules

import android.content.Context
import com.google.gson.Gson
import com.mostaqem.screens.reading.data.utils.AssetLoader
import com.mostaqem.screens.reading.data.utils.AssetReader
import com.mostaqem.screens.reading.domain.ReadingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReadingModule {


    @Provides
    @Singleton
    fun provideReadingRepository(@ApplicationContext context: Context): ReadingRepository =
        ReadingRepository(context)


}