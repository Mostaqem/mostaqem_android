package com.mostaqem.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mostaqem.features.favorites.data.FavoritedAudio
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Upsert
    suspend fun addFavorite(audio: FavoritedAudio)

    @Query("SELECT * FROM favorited_audio")
    fun getFavorities(): Flow<List<FavoritedAudio>>

    @Delete
    suspend fun removeFavorite(audio: FavoritedAudio)
}