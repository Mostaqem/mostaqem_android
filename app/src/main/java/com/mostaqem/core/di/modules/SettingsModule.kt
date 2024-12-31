package com.mostaqem.core.di.modules

import android.content.Context
import com.mostaqem.screens.settings.domain.ApkHandle
import com.mostaqem.screens.settings.domain.GithubAPI
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideGitHubApi(): GithubAPI {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideApkHandle(@ApplicationContext context: Context): ApkHandle {
        return ApkHandle(context)
    }
}