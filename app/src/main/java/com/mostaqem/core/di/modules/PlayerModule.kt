package com.mostaqem.core.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.media.service.MediaService
import com.mostaqem.screens.player.domain.PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

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
        @ApplicationContext context: Context, sessionToken: SessionToken
    ): ListenableFuture<MediaController> {
        return MediaController.Builder(context, sessionToken).buildAsync()
    }

    @Provides
    @ViewModelScoped
    fun providePlayerDao(database: AppDatabase): PlayerDao {
        return database.playerDao
    }

    @Provides
    @ViewModelScoped
    fun providePlayerRepository(dao:PlayerDao): PlayerRepository {
        return PlayerRepository(dao)
    }

}
