package com.mostaqem.features.share

import androidx.lifecycle.ViewModel
import com.mostaqem.features.reading.data.models.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ShareViewModel : ViewModel() {

    private val _verses = MutableStateFlow<Set<Verse>>(emptySet())
    val verses: StateFlow<Set<Verse>> = _verses

    fun updateVerses(veres:Set<Verse>) {
        val sortedVerses = veres.sortedBy { it.verse }.toSet()
        _verses.update  { sortedVerses }
    }


}

