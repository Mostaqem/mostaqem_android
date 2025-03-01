package com.mostaqem.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.mostaqem.features.surahs.data.Surah
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Upsert
    suspend fun insertSurah(surah: Surah)

    @Update
    suspend fun updateSurah(surah: Surah)

    @Delete
    suspend fun deleteSurah(surah: Surah)

    @Query("SELECT * FROM Surah ORDER BY lastAccessed DESC")
    fun getSurahs() : Flow<List<Surah>>

    @Query("DELETE FROM Surah WHERE lastAccessed < :oneWeekAgo")
    suspend fun deleteSurahNotAccessedPerWeek(oneWeekAgo: Long)
}