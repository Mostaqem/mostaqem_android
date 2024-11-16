package com.mostaqem.screens.home.domain

import androidx.lifecycle.viewModelScope
import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao
import kotlinx.coroutines.launch


class HomeRepository(
    private val surahDao: SurahDao,
    private val reciterDao: ReciterDao
) {

    suspend fun deleteOldSurahs() {
        val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
        surahDao.deleteSurahNotAccessedPerWeek(oneWeekAgo)
    }

    suspend fun deleteOldReciters() {
        val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
        reciterDao.deleteReciterNotAccessedPerWeek(oneWeekAgo)
    }
}