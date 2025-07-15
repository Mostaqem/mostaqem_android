package com.mostaqem.core.ui.app

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.mostaqem.core.navigation.models.AppearanceDestination
import com.mostaqem.core.navigation.models.BottomScreens
import com.mostaqem.core.navigation.models.DonationDestination
import com.mostaqem.core.navigation.models.DownloadDestination
import com.mostaqem.core.navigation.models.FavoritesDestination
import com.mostaqem.core.navigation.models.HomeDestination
import com.mostaqem.core.navigation.models.LanguagesDestination
import com.mostaqem.core.navigation.models.LoginDestination
import com.mostaqem.core.navigation.models.NotificationsDestination
import com.mostaqem.core.navigation.models.OfflineSettingsDestination
import com.mostaqem.core.navigation.models.PlayerDestination
import com.mostaqem.core.navigation.models.ReadingDestination
import com.mostaqem.core.navigation.models.SettingsDestination
import com.mostaqem.core.navigation.models.ShareDestination
import com.mostaqem.core.navigation.models.SurahsDestination
import com.mostaqem.core.navigation.models.UpdateDestination
import com.mostaqem.core.ui.controller.ObserveAsEvents
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.features.auth.presentation.LoginScreen
import com.mostaqem.features.donate.presentation.DonateScreen
import com.mostaqem.features.download_all.presentation.DownloadAlLScreen
import com.mostaqem.features.favorites.presentation.FavoritesScreen
import com.mostaqem.features.history.presentation.HistoryScreen
import com.mostaqem.features.language.presentation.LanguageScreen
import com.mostaqem.features.notifications.presentation.NotificationsScreen
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
    var isSearchExpanded by remember { mutableStateOf(false) }
    val showBottomBar =
        !isSearchExpanded && items.any { it.route::class.qualifiedName == currentRoute }
    val bottomBarHeight = 56.dp
    var hidePlayer by rememberSaveable {
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
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(bottom = if (showBottomBar) 160.dp else 0.dp)
                )
            }
        }) { padding ->
        val adaptiveInfo = currentWindowAdaptiveInfo()
        NavigationSuiteScaffold(
            layoutType = if (showBottomBar) NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                adaptiveInfo
            ) else NavigationSuiteType.None,
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
                        label = {
                            Text(
                                text = stringResource(screen.id),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        alwaysShowLabel = false,
                    )
                }
            }) {
            val playerViewModel: PlayerViewModel = hiltViewModel()
            val shareViewModel: ShareViewModel = viewModel()
            val sleepViewModel: SleepViewModel = hiltViewModel<SleepViewModel>()

            SharedTransitionLayout {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                    ) {
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
                            composable<LoginDestination> {
                                LoginScreen()
                            }
                            composable<HomeDestination> {
                                HistoryScreen(
                                    playerViewModel = playerViewModel,
                                    navController = navController,
                                    paddingValues = padding,
                                    onHideBottom = { isSearchExpanded = it }
                                )
                            }
                            composable<SurahsDestination> {
                                SurahsScreen(
                                    playerViewModel = playerViewModel,
                                    navController = navController,
                                    onHideBottom = { isSearchExpanded = it }
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
                                    surahId = surahID,
                                    recitationID = recitationID,
                                    onBack = { hidePlayer = false },
                                    onShowBar = { hidePlayer = true }
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
                                    playerViewModel = playerViewModel,
                                    onShowPlayer = { hidePlayer = false },
                                    onHidePlayer = { hidePlayer = true }
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
                            composable<NotificationsDestination> {
                                NotificationsScreen(navController = navController)
                            }
                            composable<DownloadDestination> {
                                DownloadAlLScreen(
                                    navController = navController,
                                    playerViewModel = playerViewModel
                                )
                            }
                            composable<FavoritesDestination> {
                                FavoritesScreen(
                                    navController = navController,
                                    playerViewModel = playerViewModel
                                )
                            }
                        }
                    }

                    val surah = playerViewModel.playerState.value.surah
                    if (surah != null) {
                        AnimatedVisibility(visible = !hidePlayer) {
                            PlayerBarModalSheet(
                                playerViewModel = playerViewModel,
                                navController = navController,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this
                            ) {
                                hidePlayer = true
                            }
                        }

                    }
                }
            }

        }
    }

}
