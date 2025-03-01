package com.mostaqem.core.di.modules

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.mostaqem.core.database.AppDatabase
import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.core.media.service.MediaService
import com.mostaqem.features.player.domain.PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PlayerModule {

    @Provides
    @ViewModelScoped
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken {
        return SessionToken(context, ComponentName(context, MediaService::class.java))
    }

    @OptIn(UnstableApi::class)
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
