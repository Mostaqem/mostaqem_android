package com.mostaqem.core.ui.app

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.mostaqem.core.navigation.models.Destination
import com.mostaqem.core.navigation.models.TOP_LEVEL_DESTINATIONS
import com.mostaqem.features.donate.presentation.DonateScreen
import com.mostaqem.features.download_all.presentation.DownloadAlLScreen
import com.mostaqem.features.favorites.presentation.FavoritesScreen
import com.mostaqem.features.history.presentation.HistoryScreen
import com.mostaqem.features.language.presentation.LanguageScreen
import com.mostaqem.features.notifications.presentation.NotificationsScreen
import com.mostaqem.features.offline.presentation.OfflineSettingsScreen
import com.mostaqem.features.personalization.presentation.PersonalizationViewModel
import com.mostaqem.features.personalization.presentation.ShapesScreen
import com.mostaqem.features.player.presentation.PlayerScreen
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.player.presentation.components.PlayerBarModalSheet
import com.mostaqem.features.player.presentation.components.sleep.SleepViewModel
import com.mostaqem.features.reading.presentation.ReadingScreen
import com.mostaqem.features.reciters.presentation.ReciterScreen
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.search.presentation.SearchScreen
import com.mostaqem.features.settings.presentation.SettingsScreen
import com.mostaqem.features.share.ShareScreen
import com.mostaqem.features.share.ShareViewModel
import com.mostaqem.features.surahs.presentation.SurahsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


val serializerConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(
                Destination.HomeDestination::class,
                serializer = Destination.HomeDestination.serializer()
            )
            subclass(
                Destination.SurahsDestination::class,
                serializer = Destination.SurahsDestination.serializer()
            )
            subclass(
                Destination.SettingsDestination::class,
                serializer = Destination.SettingsDestination.serializer()
            )


        }
    }
}

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(
        startRoute = Destination.HomeDestination,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )
    val navigator: Navigator = remember { Navigator(navigationState) }
    val playerViewModel: PlayerViewModel = hiltViewModel()
    var hidePlayer by remember { mutableStateOf(false) }
    val shareViewModel: ShareViewModel = viewModel()
    val sleepViewModel: SleepViewModel = hiltViewModel<SleepViewModel>()
    val personalizationViewModel: PersonalizationViewModel = hiltViewModel()
    val reciterViewModel: ReciterViewModel = hiltViewModel()
    var showBottomBar by remember { mutableStateOf(true) }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            if (showBottomBar) {
                AppNavigationBar(
                    selectedKey = navigationState.topLevelRoute,
                    onSelectKey = { navigator.navigate(it) }
                )
            }

        },
        snackbarHost = {
//            SnackbarHost(snackbarHostState) { data ->
//                Snackbar(
//                    snackbarData = data,
//                    modifier = Modifier.padding(bottom = if (showBottomBar) 160.dp else 0.dp)
//                )
//            }
        }) { paddingValues ->
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            NavDisplay(
                modifier = Modifier.align(alignment = Alignment.TopStart),
                onBack = navigator::goBack,
                entries = navigationState.toEntries(entryProvider {
                    entry<Destination.HomeDestination> {
                        HistoryScreen(
                            playerViewModel = playerViewModel,
                            onHideBottom = { },
                            navigator = navigator
                        )
                    }
                    entry<Destination.SurahsDestination> {
                        SurahsScreen(
                            playerViewModel = playerViewModel,
                            onHideBottom = {}
                        )
                    }
                    entry<Destination.SettingsDestination> {
                        SettingsScreen(navController = navigator){
                            showBottomBar=false
                        }
                    }
                    entry<Destination.PlayerDestination> {
                        PlayerScreen(
                            playerViewModel = playerViewModel,
                            sleepViewModel = sleepViewModel,
                            onBack = {
                                hidePlayer = false
                                showBottomBar = true
                            },
                            navigator = navigator,
                            onShowBar = {
                                hidePlayer = true
                                showBottomBar = false
                            },
                            )
                    }
                    entry<Destination.FavoritesDestination> {
                        FavoritesScreen(
                            navController = navigator,
                            playerViewModel = playerViewModel
                        )
                    }
                    entry<Destination.ReadingDestination> {
                        ReadingScreen(
                            chapterNumber = it.surahID,
                            navController = navigator,
                            sharingViewModel = shareViewModel
                        )
                    }
                    entry<Destination.ShareDestination> {
                        ShareScreen(
                            shareViewModel = shareViewModel,
                            navController = navigator,
                            chapterNumber = it.chapterNumber.toString(),
                            playerViewModel = playerViewModel,
                        )
                    }

                    entry<Destination.OfflineSettingsDestination> {
                        OfflineSettingsScreen(
                            navController = navigator
                        ){
                            navigator.goBack()
                            showBottomBar = true
                        }
                    }

                    entry<Destination.DonationDestination> {
                        DonateScreen(
                            navController = navigator
                        )
                    }

                    entry<Destination.LanguagesDestination> {
                        LanguageScreen(
                            playerViewModel = playerViewModel
                        ){
                            navigator.goBack()
                            showBottomBar = true
                        }
                    }

                    entry<Destination.NotificationsDestination> {
                        NotificationsScreen{
                            navigator.goBack()

                            showBottomBar = true}
                    }

                    entry<Destination.DownloadDestination> {
                        DownloadAlLScreen(
                            navController = navigator,
                        )
                    }

                    entry<Destination.ReciterDestination> {
                        ReciterScreen(
                            playerViewModel = playerViewModel,
                            navController = navigator,
                            viewModel = reciterViewModel,
                            personViewModel = personalizationViewModel
                        )
                    }

                    entry<Destination.SearchDestination> {
                        SearchScreen(
                            playerViewModel = playerViewModel,
                            navController = navigator,
                            reciterViewModel = reciterViewModel,
                            personViewModel = personalizationViewModel
                        )
                    }

                })
            )
            if (!hidePlayer) {
                PlayerBarModalSheet(
                    navigator = navigator,
                    onClear = { playerViewModel.clear() },
                    onPlayPause = { playerViewModel.handlePlayPause() },
                    playPauseIcon = playerViewModel.playPauseIcon.collectAsStateWithLifecycle(),
                    playerState = playerViewModel.playerState.value,
                    positionPercentage = playerViewModel.positionPercentage.collectAsStateWithLifecycle()
                )
            }
        }


    }


}