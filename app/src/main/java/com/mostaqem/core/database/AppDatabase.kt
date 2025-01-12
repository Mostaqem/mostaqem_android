package com.mostaqem.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.surahs.data.Surah

//@Database(
//    entities = [Surah::class, Reciter::class],
//    version = 2,
//
//)
//abstract class AppDatabase : RoomDatabase() {
//    abstract val surahDao: SurahDao
//    abstract val reciterDao: ReciterDao
//}