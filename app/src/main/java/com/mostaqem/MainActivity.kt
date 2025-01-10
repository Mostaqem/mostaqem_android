package com.mostaqem

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mostaqem.core.navigation.AppearanceDestination
import com.mostaqem.core.navigation.BottomScreens
import com.mostaqem.core.navigation.HomeDestination
import com.mostaqem.core.navigation.SettingsDestination
import com.mostaqem.core.navigation.SurahsDestination
import com.mostaqem.core.navigation.UpdateDestination
import com.mostaqem.core.ui.controller.ObserveAsEvents
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.screens.home.presentation.HomeScreen
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.player.presentation.components.PlayerBarModalSheet
import com.mostaqem.screens.settings.domain.AppSettingsSerializer
import com.mostaqem.screens.settings.presentation.SettingsScreen
import com.mostaqem.screens.settings.presentation.components.AppearanceScreen
import com.mostaqem.screens.settings.presentation.components.UpdateScreen
import com.mostaqem.screens.surahs.presentation.SurahsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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


val Context.dataStore by dataStore(fileName = "app-settings.json", serializer = AppSettingsSerializer)

@Composable
fun MostaqemApp() {
    val items = listOf(BottomScreens.Home, BottomScreens.Surah, BottomScreens.Settings)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: HomeDestination::class.qualifiedName
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val showBottomBar = remember(currentRoute) {
        items.any { it.route::class.qualifiedName == currentRoute }
    }
    val bottomBarHeight = 56.dp
    val bottomBarOffset by animateFloatAsState(
        targetValue = if (showBottomBar) 0f else 1.4f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "bottomBarOffset"
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ObserveAsEvents(flow = SnackbarController.events, snackbarHostState) { event ->
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result =
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action?.name
                    )

                if (result == SnackbarResult.ActionPerformed) {
                    event.action?.action?.invoke()
                }
            }

        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(snackbarData = data, modifier = Modifier.padding(bottom = 160.dp))
                }
            }
        ) { padding ->
            NavigationSuiteScaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(0, (bottomBarHeight.toPx() * bottomBarOffset).toInt())
                    },

                navigationSuiteItems = {
                    if (showBottomBar) {
                        items.forEach { screen ->
                            val isSelected: Boolean =
                                screen.route::class.qualifiedName == currentRoute
                            item(
                                selected = isSelected,
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
                                    if (isSelected) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = screen.selectedIcon),
                                            contentDescription = "selected"
                                        )
                                    } else {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = screen.icon),
                                            contentDescription = screen.name
                                        )
                                    }

                                },
                                label = { Text(text = screen.name) },
                                alwaysShowLabel = false,
                            )
                        }

                    }
                }) {
                val playerViewModel: PlayerViewModel = hiltViewModel()
                var isPlayerShown by rememberSaveable {
                    mutableStateOf(false)
                }

                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {

                    Box(modifier = Modifier.align(Alignment.TopStart)) {
                        NavHost(
                            navController = navController,
                            startDestination = HomeDestination,

                            enterTransition = {
                                fadeIn(animationSpec = tween(durationMillis = 300)) +
                                        scaleIn(
                                            initialScale = 0.9f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(durationMillis = 300)) +
                                        scaleOut(
                                            targetScale = 0.9f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                            },
                            popEnterTransition = {
                                fadeIn(animationSpec = tween(durationMillis = 300)) +
                                        scaleIn(
                                            initialScale = 0.9f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                            },
                            popExitTransition = {
                                fadeOut(animationSpec = tween(durationMillis = 300)) +
                                        scaleOut(
                                            targetScale = 0.9f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                            }
                        ) {
                            composable<HomeDestination> { HomeScreen(playerViewModel) }
                            composable<SurahsDestination> {
                                SurahsScreen(
                                    playerViewModel = playerViewModel,
                                    navController = navController
                                )
                            }
                            composable<SettingsDestination> { SettingsScreen(navController = navController) }
                            composable<AppearanceDestination> {
                                AppearanceScreen(
                                    navController = navController,
                                    playerViewModel = playerViewModel
                                )
                            }
                            composable<UpdateDestination> { UpdateScreen(navController = navController) }

                        }


                    }
                    val surah = playerViewModel.playerState.value.surah
                    if (surah != null) {
                        PlayerBarModalSheet(
                            surah = surah,
                            isPlayerShown = isPlayerShown,
                            onShowPlayer = { isPlayerShown = true },
                            onDismissPlayer = { isPlayerShown = false },
                            playerViewModel = playerViewModel
                        )
                    }
                }
            }
        }
    }
}



