package com.mostaqem.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.mostaqem.features.reciters.data.reciter.Reciter
import kotlinx.coroutines.flow.Flow

@Dao
interface ReciterDao {
    @Upsert
    suspend fun insertReciter(reciter: Reciter)

    @Update
    suspend fun updateReciter(reciter: Reciter)

    @Delete
    suspend fun deleteReciter(reciter: Reciter)

    @Query("SELECT * FROM Reciter ORDER BY lastAccessed DESC")
    fun getReciters() : Flow<List<Reciter>>

    @Query("DELETE FROM Reciter WHERE lastAccessed < :oneWeekAgo")
    suspend fun deleteReciterNotAccessedPerWeek(oneWeekAgo: Long)
}