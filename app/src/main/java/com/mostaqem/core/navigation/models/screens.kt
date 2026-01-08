package com.mostaqem.core.navigation.models

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.mostaqem.R
import kotlinx.serialization.Serializable

data class BottomNavItem(val icon: Int, val title: Int, val selectedIcon: Int)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Destination.HomeDestination to BottomNavItem(
        title = R.string.history,
        icon = R.drawable.outline_home_24, selectedIcon = R.drawable.baseline_home_24,
    ),
    Destination.SurahsDestination to BottomNavItem(
        title = R.string.surahs,
        icon = R.drawable.quran_book,
        selectedIcon = R.drawable.quran_book,

        ),
    Destination.SettingsDestination to BottomNavItem(
        title = R.string.settings,
        icon = R.drawable.outline_settings_24,
        selectedIcon = R.drawable.baseline_settings_24,

        ),

    )
