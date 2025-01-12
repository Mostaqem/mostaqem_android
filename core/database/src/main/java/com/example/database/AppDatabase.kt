package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.Surah
import com.example.data.Reciter
import com.example.database.dao.ReciterDao
import com.example.database.dao.SurahDao

@Database(
    entities = [Surah::class, Reciter::class],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val surahDao: SurahDao
    abstract val reciterDao: ReciterDao
}