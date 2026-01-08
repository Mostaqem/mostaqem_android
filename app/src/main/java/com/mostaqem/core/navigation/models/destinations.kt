package com.mostaqem.core.navigation.models

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface Destination: NavKey {
    @Serializable
    data object HomeDestination : Destination, NavKey

    @Serializable
    data object ReciterDestination : Destination, NavKey

    @Serializable
    data object SettingsDestination : Destination, NavKey

    @Serializable
    data object AppearanceDestination  : Destination, NavKey

    @Serializable
    data object UpdateDestination : Destination, NavKey

    @Serializable
    data object SurahsDestination : Destination, NavKey

    @Serializable
    data object OfflineSettingsDestination : Destination, NavKey

    @Serializable
    data class ReadingDestination(
        val surahID: Int,
        val surahName: String,

        ) : Destination, NavKey

    @Serializable
    data object PlayerDestination : Destination, NavKey

    @Serializable
    data object LanguagesDestination : Destination, NavKey

    @Serializable
    data object NotificationsDestination : Destination, NavKey

    @Serializable
    data object DonationDestination : Destination, NavKey

    @Serializable
    data class ShareDestination(
        val chapterNumber: Int
    ) : Destination, NavKey

    @Serializable
    data object DownloadDestination : Destination, NavKey

    @Serializable
    data object FavoritesDestination : Destination, NavKey

    @Serializable
    data object ShapesDestination : Destination, NavKey

    @Serializable
    data object SearchDestination : Destination, NavKey

}

