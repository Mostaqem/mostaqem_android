package com.mostaqem.core.navigation.models

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.mostaqem.R
import kotlinx.serialization.Serializable


@Serializable
sealed class BottomScreens<T>(
    val icon: Int,
    val route: T,
    val selectedIcon: Int,
    val id: Int,
) {
    @Serializable
    data object Home : BottomScreens<HomeDestination>(
        id = R.string.home,
        icon = R.drawable.outline_home_24,
        route = HomeDestination,
        selectedIcon = R.drawable.baseline_home_24,
    )

    @Serializable
    data object Surah : BottomScreens<SurahsDestination>(
        id = R.string.surahs,
        icon = R.drawable.quran_book,
        route = SurahsDestination,
        selectedIcon = R.drawable.quran_book,

        )

    @Serializable
    data object Settings : BottomScreens<SettingsDestination>(
        id = R.string.settings,
        icon = R.drawable.outline_settings_24,
        route = SettingsDestination,
        selectedIcon = R.drawable.baseline_settings_24,

        )


}