package com.mostaqem.core.di.modules

import android.content.Context
import com.mostaqem.features.language.domain.LanguageManager
import com.mostaqem.features.personalization.domain.PersonalizationRepository
import com.mostaqem.features.reciters.domain.ReciterRepository
import com.mostaqem.features.update.domain.ApkHandle
import com.mostaqem.features.update.domain.GithubAPI
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideGitHubApi(okHttpClient: OkHttpClient): GithubAPI {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GithubAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideApkHandle(): ApkHandle {
        return ApkHandle()
    }

    @Provides
    @Singleton
    fun providePersonalizationRepository(
        @ApplicationContext context: Context,
        repository: ReciterRepository
    ): PersonalizationRepository {
        return PersonalizationRepository(context, repository)
    }

    @Provides
    @Singleton
    fun provideLanguageManager(
        @ApplicationContext context: Context,
    ): LanguageManager {
        return LanguageManager(context = context)
    }


}