package com.mostaqem.core.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.mostaqem.core.media.service.MediaService
import com.mostaqem.screens.home.data.repository.HomeRepositoryImpl
import com.mostaqem.screens.home.domain.HomeService
import com.mostaqem.screens.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideHomeService(
        okHttpClient: OkHttpClient
    ): HomeService {
        val retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://mostaqem-api.onrender.com/api/v1/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(okHttpClient)
                .build()
        return retrofit.create(HomeService::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(api: HomeService): HomeRepository {
        return HomeRepositoryImpl(api)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
object PlayerModule {

    @Provides
    @ViewModelScoped
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken {
        return SessionToken(context, ComponentName(context, MediaService::class.java))
    }

    @Provides
    @ViewModelScoped
    fun provideMediaController(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> {
        return MediaController.Builder(context, sessionToken).buildAsync()
    }

}
