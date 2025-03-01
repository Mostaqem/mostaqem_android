package com.mostaqem.features.player.domain

import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.features.player.data.PlayerSurah
import javax.inject.Inject

class PlayerRepository @Inject constructor(private val dao: PlayerDao) {
    suspend fun getPlayer(): PlayerSurah?{
        return dao.getPlayer()
    }

    suspend fun savePlayer(player: PlayerSurah){
        dao.savePlayer(player)
    }
}