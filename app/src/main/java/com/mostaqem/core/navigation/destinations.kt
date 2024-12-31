package com.mostaqem.core.navigation

import com.mostaqem.screens.player.data.PlayerSurah
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Serializable
object SettingsDestination

@Serializable
object AppearanceDestination

@Serializable
object UpdateDestination

@Serializable
object SurahsDestination

@Serializable
data class ReadingDestination(
    val surahID: Int,
    val surahName: String
)

@Serializable
data class ScreenshotDestination(
    val message: String,
    val surahName: String,
    val verseNumber: Int,
)
