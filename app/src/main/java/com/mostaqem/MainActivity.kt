package com.mostaqem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mostaqem.core.navigation.BottomScreens
import com.mostaqem.core.navigation.HomeScreen
import com.mostaqem.core.navigation.SettingsScreen
import com.mostaqem.core.navigation.SurahsScreen
import com.mostaqem.screens.home.presentation.HomeScreen
import com.mostaqem.screens.player.presentation.PlayerScreen
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.player.presentation.components.PlayerBar
import com.mostaqem.screens.settings.SettingsScreen
import com.mostaqem.screens.surahs.presentation.SurahsScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostaqemApp() {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    val items = listOf(BottomScreens.Home, BottomScreens.Surah, BottomScreens.Settings)
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: HomeScreen::class.qualifiedName
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        NavigationSuiteScaffold(modifier = Modifier.fillMaxSize(), navigationSuiteItems = {
            items.forEach { screen ->
                val isSelected: Boolean = screen.route::class.qualifiedName == currentRoute
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
        }) {
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                val playerViewModel: PlayerViewModel = hiltViewModel()
                val sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
                var isPlayerShown by rememberSaveable {
                    mutableStateOf(false)
                }
                val playerIcon by playerViewModel.playPauseIcon.collectAsState()
                val positionPercentage by playerViewModel.positionPercentage.collectAsState()
                val currentPosition by playerViewModel.currentPosition.collectAsState()
                val duration by playerViewModel.duration.collectAsState()
                Box(modifier = Modifier.align(Alignment.TopStart)) {
                    NavHost(navController = navController, startDestination = HomeScreen) {
                        composable<HomeScreen> { HomeScreen(playerViewModel) }
                        composable<SurahsScreen> { SurahsScreen(playerViewModel = playerViewModel) }
                        composable<SettingsScreen> { SettingsScreen() }
                    }
                }

                if (playerViewModel.playerState.value.surah != null) {
                    Box(modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .height(80.dp)
                        .fillMaxWidth()
                        .clickable {
                            isPlayerShown = true
                        }) {
                        if (isPlayerShown) {
                            ModalBottomSheet(onDismissRequest = { isPlayerShown = false },
                                sheetState = sheetState,
                                containerColor = MaterialTheme.colorScheme.background,
                                dragHandle = {}) {
                                PlayerScreen(onBack = {
                                    isPlayerShown = false
                                },
                                    playerSurah = playerViewModel.playerState.value,
                                    onPlay = {
                                        playerViewModel.handlePlayPause()
                                    },
                                    onSeekPrevious = playerViewModel::seekPrevious,
                                    onSeekNext = playerViewModel::seekNext,
                                    onProgressCallback = {
                                        playerViewModel.seekToPosition(it)
                                    },
                                    progress = currentPosition.toFloat(),
                                    playerIcon = playerIcon,
                                    progressPercentage = positionPercentage,
                                    duration = duration.toFloat()
                                )
                            }
                        }
                        PlayerBar(
                            modifier = Modifier.background(Color.Black),
                            image = playerViewModel.playerState.value.surah!!.image,
                            playerIcon = playerIcon,
                            onPlayPause = {
                                playerViewModel.handlePlayPause()
                            },
                            surahName = playerViewModel.playerState.value.surah!!.arabicName,
                            reciterName = playerViewModel.playerState.value.reciter.arabicName,
                            progress = playerViewModel.positionPercentage.collectAsState().value
                        )
                    }
                }
            }
        }
    }
}



