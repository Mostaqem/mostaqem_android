package com.mostaqem.core.navigation

import com.mostaqem.R
import kotlinx.serialization.Serializable


@Serializable
sealed class BottomScreens<T>(
    val name: String,
    val icon: Int,
    val route: T,
    val selectedIcon: Int,
    val bottomBar: Boolean = false
) {
    @Serializable
    data object Home : BottomScreens<HomeDestination>(
        name = "الرئيسية",
        icon = R.drawable.outline_home_24,
        route = HomeDestination,
        selectedIcon = R.drawable.baseline_home_24,
        bottomBar = true
    )

    @Serializable
    data object Surah : BottomScreens<SurahsDestination>(
        name = "السور",
        icon = R.drawable.baseline_radio_button_checked_24,
        route = SurahsDestination,
        selectedIcon = R.drawable.baseline_radio_button_checked_24,
        bottomBar = true

    )

    @Serializable
    data object Settings : BottomScreens<SettingsDestination>(
        name = "اعدادات",
        icon = R.drawable.outline_settings_24,
        route = SettingsDestination,
        selectedIcon = R.drawable.baseline_settings_24,
        bottomBar = true

    )


}