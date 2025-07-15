package com.mostaqem.features.favorites.domain

import android.util.Log
import com.mostaqem.core.database.dao.FavoritesDao
import com.mostaqem.features.favorites.data.FavoritedAudio
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.toFavoritedAudio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn

class FavoritesRepository(private val dao: FavoritesDao) {

    suspend fun favorite(data: AudioData) {
        val favorited = data.toFavoritedAudio()
        dao.addFavorite(favorited)
    }

    suspend fun removeFavorite(data: FavoritedAudio) {
        dao.removeFavorite(data)
    }


    suspend fun isFavorited(surahID: String, recitationID: String): Boolean {
        val favoritesList = dao.getFavorities()
            .flowOn(Dispatchers.IO)
            .firstOrNull()
        return favoritesList?.any { it.surahID == surahID && it.recitationID == recitationID }
            ?: false
    }

    fun getFavorites(): Flow<List<FavoritedAudio>> {
        return dao.getFavorities()
    }

}