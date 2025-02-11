package com.mostaqem.screens.player.domain

import com.mostaqem.core.database.dao.PlayerDao
import com.mostaqem.screens.player.data.PlayerSurah
import javax.inject.Inject

class PlayerRepository @Inject constructor(private val dao: PlayerDao) {
    suspend fun getPlayer():PlayerSurah?{
        return dao.getPlayer()
    }

    suspend fun savePlayer(player:PlayerSurah){
        dao.savePlayer(player)
    }
}