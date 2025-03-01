package com.mostaqem.features.reading.presentation

import androidx.lifecycle.ViewModel
import com.mostaqem.features.reading.data.models.Verse
import com.mostaqem.features.reading.domain.ReadingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReadingViewModel @Inject constructor(private val repository: ReadingRepository) :
    ViewModel() {

    fun getVerses(chapterNumber: Int): List<Verse> {
        return repository.getChapterVerses(chapterNumber)
    }

    fun preloadVerses(chapterNumber: Int) {
        repository.preloadChapter(chapterNumber)
    }

    fun searchVerse(query: String, chapterNumber: Int): List<Verse> {
        return repository.searchVerse(query = query, chapterNumber = chapterNumber)
    }
}