package com.mostaqem.features.offline.domain

import android.content.Context
import android.os.Environment
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.ExoDatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import java.io.File
import java.util.concurrent.Executor

@UnstableApi
object DownloadUtil {
    private var downloadCache: Cache? = null
    private var databaseProvider: DatabaseProvider? = null
    private var downloadManager: DownloadManager? = null

    @Synchronized
    fun getDownloadCache(context: Context): Cache {

        if (downloadCache == null) {
            val downloadContentDirectory =
                File(context.getExternalFilesDir(null), Environment.DIRECTORY_DOWNLOADS)
            downloadCache = SimpleCache(
                downloadContentDirectory,
                NoOpCacheEvictor(),
                getDatabaseProvider(context)
            )
        }
        return downloadCache as Cache
    }

    fun getDatabaseProvider(context: Context): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context)
        }
        return databaseProvider!!
    }

    fun getDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {
            val databaseProvider = getDatabaseProvider(context.applicationContext)
            val downloadCache = getDownloadCache(context.applicationContext)
            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val downloadExecutor = Executor(Runnable::run)
            downloadManager = DownloadManager(
                context.applicationContext,
                databaseProvider,
                downloadCache,
                dataSourceFactory,
                downloadExecutor
            )
        }
        return downloadManager!!
    }


}