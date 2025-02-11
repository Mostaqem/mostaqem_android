package com.mostaqem.screens.home.domain

import com.mostaqem.core.database.dao.ReciterDao
import com.mostaqem.core.database.dao.SurahDao


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