package com.mostaqem.features.offline.domain

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.core.content.ContextCompat
import com.mostaqem.dataStore
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.reciters.data.RecitationData
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import java.io.File
import androidx.core.net.toUri

class OfflineRepository(private val context: Context) {
    fun downloadAudio(data: PlayerSurah) {

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val surahID = data.surah!!.id
        val recitationID = data.recitationID
        val request = DownloadManager.Request(data.url?.toUri()).apply {
            setTitle(data.surah.arabicName)
            setDescription("Downloading...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                "${surahID}-${recitationID}.mp3"
            )
        }

        val downloadId = downloadManager.enqueue(request)
        val receiver = object : BroadcastReceiver() {
            @SuppressLint("Range")
            override fun onReceive(context: Context, intent: Intent) {
                val completedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (completedId == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(statusIndex)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                            val fileUri = cursor.getString(uriIndex)
                            if (fileUri != null) {
                                val uri = fileUri.toUri()
                                if (uri.scheme == "file") {
                                    val filePath = uri.path
                                    if (filePath != null) {
                                        updateMetadata(filePath, data)
                                    }
                                }
                            }
                        }
                    }
                    cursor.close()
                    context.unregisterReceiver(this)
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun updateMetadata(filePath: String, data: PlayerSurah) {
        try {
            val file = File(filePath)
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            tag.setField(FieldKey.ARTIST, data.reciter.arabicName)
            tag.setField(FieldKey.MUSICIP_ID, data.surah?.id.toString())
            tag.setField(FieldKey.SUBTITLE, data.surah?.complexName)
            tag.setField(FieldKey.GENRE, data.reciter.englishName)
            tag.setField(FieldKey.ALBUM, data.reciter.id.toString())
            tag.setField(FieldKey.TITLE, data.surah?.arabicName ?: "no title")
            tag.setField(FieldKey.ALBUM_ARTIST, data.recitationID.toString())
            tag.setField(ArtworkFactory.createLinkedArtworkFromURL(data.surah?.image ?: "no image"))
            audioFile.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAudioDataFiles(): List<AudioData> {
        val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (folder == null) {
            Log.e("OfflineRepository", "External files directory is null")
            return emptyList()
        }
        if (!folder.exists() || !folder.isDirectory) {
            Log.e(
                "OfflineRepository",
                "Folder does not exist or is not a directory: ${folder.path}"
            )
            return emptyList()
        }
        val file = folder.listFiles()
        if (file == null) {
            Log.e("OfflineRepository", "listFiles() returned null for folder: ${folder.path}")
            return emptyList()
        }
        val audios = mutableListOf<AudioData>()
        file.forEach { fileItem ->
            try {
                val player = getAudioDataFromFile(fileItem)
                audios.add(player)
            } catch (e: Exception) {
                Log.e("OfflineRepository", "Error processing file ${fileItem.path}: ${e.message}")
            }
        }
        return audios
    }

    private fun getFilenames(): List<String> {
        val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val audios = mutableListOf<String>()
        val file = folder?.listFiles()
        file?.forEach {
            audios.add(it.nameWithoutExtension)
        }
        return audios
    }

    fun getFileURL(surahID: Int, recitationID: Int): AudioData? {
        val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val files = folder?.listFiles()
        val fileName = "${surahID}-${recitationID}"
        files?.forEach {
            if (it.nameWithoutExtension == fileName) {
                return getAudioDataFromFile(it)
            }
        }

        return null
    }

    private fun getAudioDataFromFile(file: File): AudioData {
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tag
        val title: String = tag.getFirst(FieldKey.TITLE)
        val complexName: String = tag.getFirst(FieldKey.SUBTITLE)
        val artist: String = tag.getFirst(FieldKey.ARTIST)
        val surahID = tag.getFirst(FieldKey.MUSICIP_ID).toInt()
        val recitationID = tag.getFirst(FieldKey.ALBUM_ARTIST).toInt()
        val englishName: String = tag.getFirst(FieldKey.GENRE)
        return AudioData(
            url = file.absolutePath,
            surah = Surah(
                arabicName = title,
                id = surahID,
                image = "https://img.freepik.com/premium-photo/illustration-mosque-with-crescent-moon-stars-simple-shapes-minimalist-flat-design_217051-15556.jpg",
                complexName = complexName,
                revelationPlace = "",
                versusCount = 0,
                lastAccessed = 0,
            ),
            recitationID = recitationID,
            recitation = RecitationData(
                reciter = Reciter(
                    arabicName = artist,
                    id = 1,
                    image = "",
                    lastAccessed = 0,
                    englishName = englishName
                ),
                id = recitationID,
                name = "",
                englishName = "",
                reciterID = 1
            ),
            size = getDirectorySize(file)
        )
    }

    suspend fun changePlayOption(value: Boolean) {
        context.dataStore.updateData { settings ->
            settings.copy(playDownloaded = value)
        }
    }

    fun isSurahDownloaded(surahID: Int, recitationID: Int): Boolean {
        val files = getFilenames()
        return files.contains("${surahID}-${recitationID}")
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

    fun deleteFile(path: String) {
        File(path).delete()
    }


}