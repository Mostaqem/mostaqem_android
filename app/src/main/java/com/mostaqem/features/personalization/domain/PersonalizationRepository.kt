package com.mostaqem.features.personalization.domain

import android.content.Context
import com.mostaqem.dataStore
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.domain.ReciterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalizationRepository @Inject constructor(
    private val context: Context,
    private val reciterRepository: ReciterRepository
) {

    fun getDefaultReciter(): Flow<Reciter> = context.dataStore.data
        .map {
            it.reciterSaved
        }

    suspend fun saveDefaultReciter(reciter: Reciter) {
        context.dataStore.updateData {
            it.copy(reciterSaved = reciter)
        }
        val defaultReciter = getDefaultReciter().first()
        val firstRecitation =
            reciterRepository.getRemoteRecitations(defaultReciter.id).response.first {
                it.name.contains("حفص عن عاصم")
            }
        changeRecitationID(firstRecitation.id)
    }


    suspend fun changeShape(id: String) {
        context.dataStore.updateData { settings ->
            settings.copy(shapeID = id)
        }
    }

    suspend fun changeRecitationID(id: Int) {
        context.dataStore.updateData { settings ->
            settings.copy(recitationID = id)
        }
    }

    fun getDefaultRecitationID(): Flow<Int> = context.dataStore.data.map {
        it.recitationID
    }




}