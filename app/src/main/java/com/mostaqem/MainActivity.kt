package com.mostaqem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mostaqem.core.navigation.BottomScreens
import com.mostaqem.core.navigation.HomeScreen
import com.mostaqem.core.navigation.SettingsScreen
import com.mostaqem.screens.home.presentation.HomeScreen
import com.mostaqem.screens.settings.SettingsScreen
import com.mostaqem.ui.theme.MostaqemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MostaqemTheme {
                MostaqemApp()
            }
        }
    }


}

@Composable
fun MostaqemApp() {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    val items = listOf(BottomScreens.Home, BottomScreens.Settings)
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: HomeScreen::class.qualifiedName
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        NavigationSuiteScaffold(modifier = Modifier.fillMaxSize(), navigationSuiteItems = {
            items.forEach { screen ->
                item(
                    selected = screen.route::class.qualifiedName == currentRoute,
                    onClick = {
                        navController.navigate(screen.route) {

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }

                    },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = screen.icon),
                            contentDescription = screen.name
                        )
                    },
                    label = { Text(text = screen.name) },
                    alwaysShowLabel = false,
                )


            }
        }) {
            NavHost(navController = navController, startDestination = HomeScreen,
                enterTransition = { slideInHorizontally { -it } + fadeIn() },
                exitTransition = { slideOutHorizontally { it } + fadeOut() },
                popEnterTransition = { slideInHorizontally { it } + fadeIn() },
                popExitTransition = { slideOutHorizontally { -it } + fadeOut() }
            ) {
                composable<HomeScreen> {

                    HomeScreen()
                }
                composable<SettingsScreen> { SettingsScreen() }

            }


        }
    }

}
