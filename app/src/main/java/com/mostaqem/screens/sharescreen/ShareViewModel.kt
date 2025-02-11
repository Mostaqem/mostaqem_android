package com.mostaqem.screens.sharescreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.mostaqem.screens.reading.data.models.Verse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream

class ShareViewModel : ViewModel() {

    private val _verses = MutableStateFlow<Set<Verse>>(emptySet())
    val verses: StateFlow<Set<Verse>> = _verses

    fun updateVerses(veres:Set<Verse>) {
        val sortedVerses = veres.sortedBy { it.verse }.toSet()
        _verses.update  { sortedVerses }
    }


}

