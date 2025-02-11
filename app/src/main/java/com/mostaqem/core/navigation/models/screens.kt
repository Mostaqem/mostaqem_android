package com.mostaqem.core.navigation.models

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
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
        name = "ألسور",
        icon = R.drawable.quran_book,
        route = SurahsDestination,
        selectedIcon = R.drawable.quran_book,
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