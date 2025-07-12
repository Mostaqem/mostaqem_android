package com.mostaqem.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.execSQL
import com.mostaqem.core.database.converter.Converters
import com.mostaqem.core.database.dao.DownloadedAudioDao
import com.mostaqem.core.database.dao.FavoritesDao
import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.offline.data.DownloadedAudioEntity
import com.mostaqem.features.favorites.data.FavoritedAudio
import com.mostaqem.features.surahs.data.Surah

@Database(
    entities = [Surah::class, Reciter::class, PlayerSurah::class, DownloadedAudioEntity::class, FavoritedAudio::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 2, to = 3)]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val surahDao: SurahDao
    abstract val reciterDao: ReciterDao
    abstract val playerDao: PlayerDao

      abstract val audioDao: DownloadedAudioDao

    abstract val favoritesDao: FavoritesDao

    companion object{
        val migrate2To3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `downloaded_audios` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `size` INTEGER NOT NULL, `reciter` TEXT NOT NULL, `reciterID` TEXT NOT NULL, `recitationID` TEXT NOT NULL, `surahID` TEXT NOT NULL, `remoteUrl` TEXT NOT NULL, PRIMARY KEY(`id`))",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `favorited_audio` (`id` TEXT NOT NULL, `surahName` TEXT, `reciter` TEXT NOT NULL, `reciterID` TEXT NOT NULL, `recitationID` TEXT NOT NULL, `surahID` TEXT NOT NULL, `remoteUrl` TEXT NOT NULL, PRIMARY KEY(`id`))",
                )
            }
        }
    }

}

