package com.mostaqem.core.navigation

import com.mostaqem.R
import kotlinx.serialization.Serializable


@Serializable
sealed class BottomScreens<T>(
    val name: String,
    val icon: Int,
    val route: T,
    val selectedIcon: Int
) {
    @Serializable
    data object Home : BottomScreens<HomeScreen>(
        name = "الرئيسية",
        icon = R.drawable.outline_home_24,
        route = HomeScreen,
        selectedIcon = R.drawable.baseline_home_24
    )

    @Serializable
    data object Surah : BottomScreens<SurahsScreen>(
        name = "السور",
        icon = R.drawable.baseline_radio_button_checked_24,
        route = SurahsScreen,
        selectedIcon = R.drawable.baseline_radio_button_checked_24
    )

    @Serializable
    data object Settings : BottomScreens<SettingsScreen>(
        name = "اعدادات",
        icon = R.drawable.outline_settings_24,
        route = SettingsScreen,
        selectedIcon = R.drawable.baseline_settings_24
    )


}