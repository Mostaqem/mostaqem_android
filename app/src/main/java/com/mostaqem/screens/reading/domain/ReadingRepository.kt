package com.mostaqem.screens.reading.domain

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mostaqem.screens.reading.data.models.Verse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ReadingRepository(private val context: Context) {
    private var quranData: Map<String, List<Verse>>? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val chapterCache = mutableMapOf<Int, List<Verse>>()

    fun getChapterVerses(chapterNumber: Int): List<Verse> {
        return chapterCache.getOrPut(chapterNumber) {
            loadQuranData()[chapterNumber.toString()] ?: emptyList()
        }
    }

    fun preloadChapter(chapterNumber: Int) {
        if (!chapterCache.containsKey(chapterNumber)) {
            scope.launch {
                getChapterVerses(chapterNumber)
            }
        }
    }

    private fun loadQuranData(): Map<String, List<Verse>> {
        return quranData ?: try {
            val jsonString = context.assets.open("quran.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Verse>>>() {}.type
            Gson().fromJson<Map<String, List<Verse>>>(jsonString, type).also {
                quranData = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    fun searchVerse(query: String, chapterNumber: Int): List<Verse> {
        if (query.isBlank()) return emptyList()

        val chapterVerses = getChapterVerses(chapterNumber)
        val searchTerms = query.trim().split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().lowercase() }
        if (searchTerms.isEmpty()) return emptyList()

        return chapterVerses.filter { verse ->
            val verseText = verse.text.lowercase()
            searchTerms.all { term ->
                verseText.contains(term)
            }
        }.sortedBy { verse ->
            verse.verse
        }
    }

    fun searchAllChapters(query: String): List<Pair<Int, List<Verse>>> {
        if (query.isBlank()) return emptyList()

        val results = mutableListOf<Pair<Int, List<Verse>>>()

        // Get all loaded chapters from cache
        val loadedChapters = chapterCache.keys.toList()

        // Search in each loaded chapter
        loadedChapters.forEach { chapterNum ->
            val chapterResults = searchVerse(query, chapterNum)
            if (chapterResults.isNotEmpty()) {
                results.add(Pair(chapterNum, chapterResults))
            }
        }

        return results
    }
}