package com.mostaqem.core.navigation

import com.mostaqem.R
import kotlinx.serialization.Serializable


@Serializable
sealed class BottomScreens<T>(val name:String, val icon: Int, val route: T){
    @Serializable
    data object Home: BottomScreens<HomeScreen>(name = "الرئيسية", icon = R.drawable.outline_mosque_24, route = HomeScreen)

    @Serializable
    data object Settings: BottomScreens<SettingsScreen>(name = "اعدادات", icon = R.drawable.outline_settings_24, route = SettingsScreen)


}