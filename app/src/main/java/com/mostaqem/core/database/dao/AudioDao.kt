package com.mostaqem.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mostaqem.features.offline.data.DownloadedAudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedAudioDao {
    @Upsert
    suspend fun saveDownloadedAudio(data: DownloadedAudioEntity)

    @Query("SELECT * FROM downloaded_audios")
    fun getAllDownloadedAudios(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT * FROM downloaded_audios WHERE id = :audioId")
    suspend fun getDownloadedAudio(audioId: String): DownloadedAudioEntity?

    @Query("DELETE FROM downloaded_audios WHERE id = :audioID")
    suspend fun deleteDownloadedAudio(audioID: String)

}

