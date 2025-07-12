package com.mostaqem.features.offline.domain

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.NetworkStatus
import com.mostaqem.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class OfflineRepository(
    private val context: Context,
    private val manager: OfflineManager,
) {


    suspend fun changePlayOption(value: Boolean) {
        context.dataStore.updateData { settings ->
            settings.copy(playDownloaded = value)
        }
    }

    fun getPlayDownloadedOption(): Flow<Boolean> = context.dataStore.data.map {
        it.playDownloaded
    }

    private fun getDirectorySize(file: File): Long {
        if (file.isFile) {
            return file.length()
        } else if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null) {
                var size: Long = 0
                for (child in children) {
                    size += getDirectorySize(child)
                }
                return size
            }
        }
        return 0
    }

    private fun getFreeSpace(path: String): Long {
        val stat = StatFs(path)
        return stat.availableBytes
    }

    private fun calculateProgress(directorySize: Long, freeSpace: Long): Float {
        if (freeSpace == 0L) return 0f
        val totalAvailableSpace = directorySize + freeSpace
        return (directorySize.toFloat() / totalAvailableSpace.toFloat()).coerceIn(0f, 1f)
    }

    fun calculateMemoryProgress(): Float {
        val file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (file != null) {
            val directorySize = getDirectorySize(file)
            val freeSpace: Long = getFreeSpace(file.absolutePath)
            return calculateProgress(directorySize, freeSpace)
        }
        return 0f
    }

    fun deleteFile(audioId: String) {
        manager.removeDownload(audioId)

    }


}