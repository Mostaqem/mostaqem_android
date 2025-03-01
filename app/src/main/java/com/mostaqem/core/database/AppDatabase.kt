package com.mostaqem.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mostaqem.core.database.converter.Converters
import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah

@Database(
    entities = [Surah::class, Reciter::class, PlayerSurah::class],
    version = 2,
    )
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val surahDao: SurahDao
    abstract val reciterDao: ReciterDao
    abstract val playerDao:PlayerDao

}

