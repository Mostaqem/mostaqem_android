package com.mostaqem.screens.reading.data.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface AssetReader {
    fun readJson(fileName: String): String
}

@Singleton
class AssetLoader @Inject constructor(
    @ApplicationContext private val context: Context
) :AssetReader{
    override fun readJson(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}