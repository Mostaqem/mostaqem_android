package com.mostaqem.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mostaqem.features.player.data.PlayerSurah

@Dao
interface PlayerDao {
    @Upsert
    suspend fun savePlayer(player: PlayerSurah)

    @Query("SELECT * FROM PlayerSurah LIMIT 1")
    suspend fun getPlayer(): PlayerSurah?
}