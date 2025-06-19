package com.mostaqem.core.navigation.models

import kotlinx.serialization.Serializable

@Serializable
object LoginDestination

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
object OfflineSettingsDestination

@Serializable
data class ReadingDestination(
    val surahID: Int,
    val surahName: String,

)

@Serializable
object PlayerDestination

@Serializable
object LanguagesDestination

@Serializable
object NotificationsDestination

@Serializable
object DonationDestination

@Serializable
data class ShareDestination(
    val chapterName: String
)