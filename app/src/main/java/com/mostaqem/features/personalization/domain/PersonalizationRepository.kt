package com.mostaqem.features.personalization.domain

import android.content.Context
import android.util.Log
import com.mostaqem.dataStore
import com.mostaqem.features.language.data.Language
import com.mostaqem.features.language.domain.AppLanguages
import com.mostaqem.features.reciters.data.Recitation
import com.mostaqem.features.reciters.data.RecitationData
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.domain.ReciterRepository
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.data.SurahSortBy
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

    val appSettings: Flow<AppSettings> = context.dataStore.data

    fun getDefaultReciter(): Flow<Reciter> = context.dataStore.data
        .map {
            it.reciterSaved
        }

    suspend fun saveDefaultReciter(reciter: Reciter) {
        context.dataStore.updateData {
            it.copy(reciterSaved = reciter)
        }
        val firstRecitation =
            reciterRepository.getRemoteRecitations(reciter.id).response.first().copy(reciter = reciter)
        changeRecitationID(recitationData = firstRecitation)
    }


    suspend fun changeShape(id: String) {
        context.dataStore.updateData { settings ->
            settings.copy(shapeID = id)
        }
    }

    suspend fun changeRecitationID(recitationData: RecitationData) {
        context.dataStore.updateData { settings ->
            settings.copy(recitation = recitationData)
        }
    }

    fun getDefaultRecitation(): Flow<RecitationData> = context.dataStore.data.map {
        it.recitation
    }

    suspend fun changeLanguageDatastore(language: AppLanguages) {
        context.dataStore.updateData { it.copy(language = language) }
    }

    suspend fun changeSortBy(sortBy: SurahSortBy) {
        context.dataStore.updateData { settings -> settings.copy(sortBy = sortBy) }
    }

    suspend fun changeFridayNotification(value: Boolean) {
        context.dataStore.updateData { settings -> settings.copy(fridayNotificationEnabled = value) }
    }

    suspend fun changeNightsNotification(value: Boolean) {
        context.dataStore.updateData { settings -> settings.copy(almulkNotificationEnabled = value) }
    }



}