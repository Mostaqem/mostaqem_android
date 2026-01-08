package com.mostaqem.core.ui.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mostaqem.core.navigation.models.Destination
import com.mostaqem.core.ui.controller.ObserveAsEvents
import com.mostaqem.core.ui.controller.SnackbarController
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MostaqemApp() {

      val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


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
//
//    val bottomBarOffset by animateFloatAsState(
//        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
//        targetValue = if (showBottomBar) 0f else 1.4f, label = "bottomBarOffset"
//    )


    Scaffold() { paddingValues ->
        NavigationRoot(modifier = Modifier.padding(paddingValues))
    }
//    Scaffold(
//        contentWindowInsets = WindowInsets.systemBars,
//        bottomBar = {
//            AppNavigationBar(
//                selectedKey = Destination.HomeDestination,
//                onSelectKey = {}
//            )
//        },
//        snackbarHost = {
//            SnackbarHost(snackbarHostState) { data ->
//                Snackbar(
//                    snackbarData = data,
//                    modifier = Modifier.padding(bottom = if (showBottomBar) 160.dp else 0.dp)
//                )
//            }
//        }) { padding ->
//        NavigationSuiteScaffold(
//            layoutType = if (showBottomBar) NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
//                adaptiveInfo
//            ) else NavigationSuiteType.None,
//
//            navigationSuiteItems = {
//                items.forEach { screen ->
//                    val isSelected: Boolean = screen.route::class.qualifiedName == currentRoute
//                    item(
//                        modifier = Modifier.offset {
//                            IntOffset(
//                                0, (bottomBarHeight.toPx() * bottomBarOffset).toInt()
//                            )
//                        },
//                        selected = isSelected,
//                        onClick = {
//                            navController.navigate(screen.route) {
//                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        },
//                        icon = {
//                            if (isSelected) {
//                                Icon(
//                                    imageVector = ImageVector.vectorResource(id = screen.selectedIcon),
//                                    modifier = Modifier.size(22.dp),
//                                    contentDescription = "selected"
//                                )
//                            } else {
//                                Icon(
//                                    imageVector = ImageVector.vectorResource(id = screen.icon),
//                                    modifier = Modifier.size(22.dp),
//                                    contentDescription = "nonselected"
//                                )
//                            }
//                        },
//                        label = {
//                            Text(
//                                text = stringResource(screen.id),
//                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
//                            )
//                        },
//                        alwaysShowLabel = false,
//                    )
//                }
//            }) {
//            val playerViewModel: PlayerViewModel = hiltViewModel()
//            val shareViewModel: ShareViewModel = viewModel()
//            val sleepViewModel: SleepViewModel = hiltViewModel<SleepViewModel>()
//            val personalizationViewModel: PersonalizationViewModel = hiltViewModel()
//            val reciterViewModel: ReciterViewModel = hiltViewModel()
//            SharedTransitionLayout {
//                Box(
//                    contentAlignment = Alignment.BottomCenter,
//                    modifier = Modifier.fillMaxSize(),
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .align(Alignment.TopStart)
//                    ) {
//                        NavHost(
//                            navController = navController,
//                            startDestination = HomeDestination,
//                            enterTransition = {
//                                fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
//                                    initialScale = 0.9f,
//                                    animationSpec = tween(durationMillis = 300)
//                                )
//                            },
//                            exitTransition = {
//                                fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(
//                                    targetScale = 0.9f,
//                                    animationSpec = tween(durationMillis = 300)
//                                )
//                            },
//                            popEnterTransition = {
//                                fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
//                                    initialScale = 0.9f,
//                                    animationSpec = tween(durationMillis = 300)
//                                )
//                            },
//                            popExitTransition = {
//                                fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(
//                                    targetScale = 0.9f,
//                                    animationSpec = tween(durationMillis = 300)
//                                )
//                            }) {
//                            composable<LoginDestination> {
//                                LoginScreen()
//                            }
//                            composable<HomeDestination> {
//                                HistoryScreen(
//                                    playerViewModel = playerViewModel,
//                                    navController = navController,
//                                    onHideBottom = { isSearchExpanded = it }
//                                )
//                            }
//                            composable<SurahsDestination> {
//                                SurahsScreen(
//                                    playerViewModel = playerViewModel,
//                                    navController = navController,
//                                    onHideBottom = { isSearchExpanded = it }
//                                )
//                            }
//                            composable<SettingsDestination> { SettingsScreen(navController = navController) }
//                            composable<AppearanceDestination> {
//                                AppearanceScreen(
//                                    navController = navController,
//                                    playerViewModel = playerViewModel
//                                )
//                            }
//                            composable<UpdateDestination> { UpdateScreen(navController = navController) }
//                            composable<PlayerDestination>(
//                                deepLinks = listOf(
//                                    navDeepLink<PlayerDestination>(basePath = "mostaqem://player"),
//                                    navDeepLink<PlayerDestination>(basePath = "https://mostaqemapp.online/quran/{surahID}/{recitationID}"),
//                                ),
//
//                                ) {
//                                val surahID = it.arguments?.getString("surahID")?.toInt()
//                                val recitationID = it.arguments?.getString("recitationID")?.toInt()
//                                PlayerScreen(
//                                    playerViewModel = playerViewModel,
//                                    sleepViewModel = sleepViewModel,
//                                    sharedTransitionScope = this@SharedTransitionLayout,
//                                    animatedVisibilityScope = this,
//                                    navController = navController,
//                                    surahId = surahID,
//                                    recitationID = recitationID,
//                                    onBack = { },
//                                    onShowBar = { }
//                                )
//                            }
//                            composable<ReadingDestination> {
//                                val args = it.toRoute<ReadingDestination>()
//                                ReadingScreen(
//                                    chapterName = args.surahName,
//                                    chapterNumber = args.surahID,
//                                    navController = navController,
//                                    sharingViewModel = shareViewModel
//                                )
//                            }
//                            composable<ShareDestination> {
//                                val args = it.toRoute<ShareDestination>()
//                                ShareScreen(
//                                    shareViewModel = shareViewModel,
//                                    navController = navController,
//                                    chapterNumber = args.chapterNumber.toString(),
//                                    playerViewModel = playerViewModel,
//                                )
//                            }
//                            composable<OfflineSettingsDestination> {
//                                OfflineSettingsScreen(navController = navController)
//                            }
//                            composable<DonationDestination> {
//                                DonateScreen(navController = navController)
//                            }
//                            composable<LanguagesDestination> {
//                                LanguageScreen(
//                                    navController = navController,
//                                    playerViewModel = playerViewModel
//                                )
//                            }
//                            composable<NotificationsDestination> {
//                                NotificationsScreen(navController = navController)
//                            }
//                            composable<DownloadDestination> {
//                                DownloadAlLScreen(
//                                    navController = navController,
//                                    playerViewModel = playerViewModel
//                                )
//                            }
//                            composable<FavoritesDestination> {
//                                FavoritesScreen(
//                                    navController = navController,
//                                    playerViewModel = playerViewModel
//                                )
//                            }
//                            composable<ShapesDestination> {
//                                ShapesScreen(
//                                    navController = navController,
//                                    viewModel = personalizationViewModel
//                                )
//                            }
//                            composable<ReciterDestination> {
//                                ReciterScreen(
//                                    playerViewModel = playerViewModel,
//                                    navController = navController,
//                                    viewModel = reciterViewModel,
//                                    personViewModel = personalizationViewModel
//                                )
//                            }
//                            composable<SearchDestination> {
//                                SearchScreen(
//                                    playerViewModel = playerViewModel,
//                                    navController = navController,
//                                    reciterViewModel = reciterViewModel,
//                                    personViewModel = personalizationViewModel
//                                )
//                            }
//                        }
//                    }
//
//                    if (!hidePlayer) {
//                        PlayerBarModalSheet(
//                            playerViewModel = playerViewModel,
//                            navController = navController,
//                        )
//                    }
//
//
//                }
//            }
//
//        }
}

//}
