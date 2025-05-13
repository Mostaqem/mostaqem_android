package com.mostaqem.core.ui.app

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.mostaqem.core.navigation.models.AppearanceDestination
import com.mostaqem.core.navigation.models.BottomScreens
import com.mostaqem.core.navigation.models.DonationDestination
import com.mostaqem.core.navigation.models.HomeDestination
import com.mostaqem.core.navigation.models.LanguagesDestination
import com.mostaqem.core.navigation.models.OfflineSettingsDestination
import com.mostaqem.core.navigation.models.PlayerDestination
import com.mostaqem.core.navigation.models.ReadingDestination
import com.mostaqem.core.navigation.models.SettingsDestination
import com.mostaqem.core.navigation.models.ShareDestination
import com.mostaqem.core.navigation.models.SurahsDestination
import com.mostaqem.core.navigation.models.UpdateDestination
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.NetworkStatus
import com.mostaqem.core.ui.controller.ObserveAsEvents
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.features.donate.presentation.DonateScreen
import com.mostaqem.features.history.presentation.HistoryScreen
import com.mostaqem.features.language.presentation.LanguageScreen
import com.mostaqem.features.offline.presentation.OfflineSettingsScreen
import com.mostaqem.features.personalization.presentation.AppearanceScreen
import com.mostaqem.features.player.presentation.PlayerScreen
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.player.presentation.components.PlayerBarModalSheet
import com.mostaqem.features.player.presentation.components.sleep.SleepViewModel
import com.mostaqem.features.reading.presentation.ReadingScreen
import com.mostaqem.features.settings.presentation.SettingsScreen
import com.mostaqem.features.share.ShareScreen
import com.mostaqem.features.share.ShareViewModel
import com.mostaqem.features.surahs.presentation.SurahsScreen
import com.mostaqem.features.update.presentation.UpdateScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MostaqemApp() {
    val items = listOf(
        BottomScreens.Home,
        BottomScreens.Surah,
        BottomScreens.Settings
    )
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
    val hidePlayer = rememberSaveable {
        mutableStateOf(false)
    }
    val bottomBarOffset by animateFloatAsState(
        targetValue = if (showBottomBar) 0f else 1.4f, animationSpec = tween(
            durationMillis = 300, easing = FastOutSlowInEasing
        ), label = "bottomBarOffset"
    )




    ObserveAsEvents(flow = SnackbarController.events, snackbarHostState) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = event.message, actionLabel = event.action?.name
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }
    Scaffold(snackbarHost = {
        SnackbarHost(snackbarHostState) { data ->
            Snackbar(
                snackbarData = data,
                modifier = Modifier.padding(bottom = if (showBottomBar) 160.dp else 0.dp)
            )
        }
    }) { padding ->
        NavigationSuiteScaffold(
            layoutType = if (showBottomBar) NavigationSuiteType.NavigationBar else NavigationSuiteType.None,
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    3.dp
                )
            ),
            modifier = Modifier.fillMaxSize(),

            navigationSuiteItems = {

                items.forEach { screen ->
                    val isSelected: Boolean = screen.route::class.qualifiedName == currentRoute
                    item(
                        modifier = Modifier.offset {
                            IntOffset(
                                0, (bottomBarHeight.toPx() * bottomBarOffset).toInt()
                            )
                        },

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
                                    modifier = Modifier.size(22.dp),
                                    contentDescription = "selected"
                                )
                            } else {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = screen.icon),
                                    modifier = Modifier.size(22.dp),
                                    contentDescription = "nonselected"
                                )
                            }

                        },
                        label = { Text(text = stringResource(screen.id)) },
                        alwaysShowLabel = false,
                    )
                }


            }) {
            val playerViewModel: PlayerViewModel = hiltViewModel()
            val shareViewModel: ShareViewModel = viewModel()
            val sleepViewModel: SleepViewModel = hiltViewModel<SleepViewModel>()

            SharedTransitionLayout {
                Box(
                    contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()
                ) {

                    Box(modifier = Modifier.align(Alignment.TopStart)) {

                        NavHost(
                            navController = navController,
                            startDestination = HomeDestination,

                            enterTransition = {
                                fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
                                    initialScale = 0.9f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                            },
                            exitTransition = {
                                fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(
                                    targetScale = 0.9f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                            },
                            popEnterTransition = {
                                fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
                                    initialScale = 0.9f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                            },
                            popExitTransition = {
                                fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(
                                    targetScale = 0.9f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                            }) {
                            composable<HomeDestination> {
                                HistoryScreen(
                                    playerViewModel = playerViewModel,
                                    navController = navController
                                )
                            }
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
                            composable<PlayerDestination>(
                                deepLinks = listOf(
                                    navDeepLink<PlayerDestination>(basePath = "mostaqem://player"),
                                    navDeepLink<PlayerDestination>(basePath = "https://mostaqemapp.online/quran/{surahID}/{recitationID}"),

                                    ),

                                ) {
                                val surahID = it.arguments?.getString("surahID")?.toInt()
                                val recitationID = it.arguments?.getString("recitationID")?.toInt()
                                PlayerScreen(
                                    playerViewModel = playerViewModel,
                                    sleepViewModel = sleepViewModel,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this,
                                    navController = navController,
                                    hidePlayerBar = hidePlayer,
                                    surahId = surahID,
                                    recitationID = recitationID
                                )
                            }
                            composable<ReadingDestination> {
                                val args = it.toRoute<ReadingDestination>()
                                ReadingScreen(
                                    chapterName = args.surahName,
                                    chapterNumber = args.surahID,
                                    navController = navController,
                                    sharingViewModel = shareViewModel
                                )
                            }
                            composable<ShareDestination> {
                                val args = it.toRoute<ShareDestination>()
                                ShareScreen(
                                    shareViewModel = shareViewModel,
                                    navController = navController,
                                    chapterName = args.chapterName,
                                    hidePlayerState = hidePlayer,
                                    playerViewModel = playerViewModel
                                )
                            }

                            composable<OfflineSettingsDestination> {
                                OfflineSettingsScreen(navController = navController)
                            }
                            composable<DonationDestination> {
                                DonateScreen(navController = navController)
                            }
                            composable<LanguagesDestination> {
                                LanguageScreen(
                                    navController = navController,
                                    playerViewModel = playerViewModel
                                )
                            }
                        }
                    }

                    val surah = playerViewModel.playerState.value.surah
                    val context = LocalContext.current
                    val isConnected by NetworkConnectivityObserver(context).observe()
                        .collectAsState(initial = NetworkStatus.Unavailable)
                    if (isConnected == NetworkStatus.Lost) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp
                                    )
                                )
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .height(80.dp)
                                .fillMaxWidth()
                        ) {
                            Text("غير متصل بأنترنت, حاول مرة اخري")
                        }
                    }
                    if (surah != null) {
                        AnimatedVisibility(visible = !hidePlayer.value) {
                            PlayerBarModalSheet(
                                playerViewModel = playerViewModel,
                                navController = navController,
                                hidePlayer = hidePlayer,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this
                            )
                        }

                    }
                }
            }

        }
    }

}
