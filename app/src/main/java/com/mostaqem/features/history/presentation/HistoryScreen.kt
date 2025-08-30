package com.mostaqem.features.history.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.materialkolor.ktx.harmonize
import com.materialkolor.ktx.harmonizeWithPrimary
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.core.navigation.models.FavoritesDestination
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.history.presentation.components.AdvancedSearch
import com.mostaqem.features.history.presentation.components.HistoryOnline
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.domain.ReciterEvents
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.data.SelectedSurahState
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.presentation.SurahsViewModel
import com.mostaqem.features.surahs.presentation.components.SurahOptions

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HistoryScreen(
    playerViewModel: PlayerViewModel,
    viewModel: HistoryViewModel = hiltViewModel(),
    surahViewModel: SurahsViewModel = hiltViewModel(),
    reciterViewModel: ReciterViewModel = hiltViewModel(),
    navController: NavController,
    onHideBottom: (Boolean) -> Unit,
) {
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code
    val isArabic = languageCode == "ar"
    val fontFamily = MaterialTheme.typography.titleLarge.fontFamily
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedState: SelectedSurahState? by remember { mutableStateOf(null) }
    var openOptionsSheet by remember { mutableStateOf(false) }
    val defaultReciter by playerViewModel.defaultReciterState.collectAsState()
    val defaultReciterName = if (isArabic) defaultReciter.arabicName else defaultReciter.englishName
    val context = LocalContext.current
    Scaffold(

        topBar = {
            AdvancedSearch(
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                query = uiState.searchQuery,
                onHideBottom = onHideBottom,
                queryList = uiState.queryList,
                selectedLabel = uiState.selectedFilter,
                player = playerViewModel.playerState.value,
                isArabic = languageCode == "ar",
                defaultReciterName = defaultReciterName,
                onSurahClick = {
                    playerViewModel.changeSurah(it)
                    playerViewModel.fetchMediaUrl()
                    surahViewModel.onSurahEvents(SurahEvents.AddSurah(it))
                },
                onSelect = { viewModel.onFilterSelected(it) },
                onSurahMenuClick = {
                    selectedState = SelectedSurahState(
                        surahName = it.arabicName,
                        surahID = it.id,
                    )
                    openOptionsSheet = true
                },
                onReciterclick = {
                    playerViewModel.changeReciter(it)
                    reciterViewModel.onReciterEvents(ReciterEvents.AddReciter(it))
                }

            )
        }) {
        Column(
            modifier = Modifier
                .padding(paddingValues = it)
                .verticalScroll(rememberScrollState())
        ) {
            if (openOptionsSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        openOptionsSheet = false
                    }
                ) {
                    SurahOptions(
                        playerViewModel = playerViewModel,
                        navController = navController,
                        selectedSurah = selectedState!!.surahName,
                        selectedSurahID = selectedState!!.surahID,
                        context = context
                    ) {
                        openOptionsSheet = false
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(horizontal = 30.dp, vertical = 20.dp))
            ) {
                IconButton(
                    shape = IconButtonDefaults.extraLargePressedShape,
                    onClick = { navController.navigate(FavoritesDestination) },
                    modifier = Modifier.size(
                        height = 100.dp,
                        width = 200.dp
                    ), colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.harmonize(Color.Red)
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "favorites",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.harmonize(Color.Red),
                            modifier = Modifier.size(
                                IconButtonDefaults.mediumIconSize
                            )
                        )
                        Text(
                            stringResource(R.string.favorites),
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.harmonize(Color.Red)
                        )
                    }

                }
                IconButton(
                    shape = IconButtonDefaults.extraLargePressedShape,
                    onClick = {
                        playerViewModel.playRandom()
                    },
                    modifier = Modifier.size(
                        height = 100.dp,
                        width = 200.dp
                    ),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Shuffle,
                            contentDescription = "Shuffle",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(
                                IconButtonDefaults.mediumIconSize
                            )
                        )
                        Text(
                            stringResource(R.string.shuffle),
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            HistoryOnline(
                playerViewModel = playerViewModel,
                isArabic = languageCode == "ar",
                fontFamily = fontFamily!!,
                state = uiState,
                onCardClick = { surah ->
                    selectedState = SelectedSurahState(
                        surahName = if (isArabic) surah.arabicName else surah.complexName,
                        surahID = surah.id,

                        )
                    openOptionsSheet = true
                },
                onAddSurah = { audio ->
                    playerViewModel.playAudioData(audio)
                    surahViewModel.onSurahEvents(
                        SurahEvents.AddSurah(
                            audio.surah
                        )
                    )
                    reciterViewModel.onReciterEvents(
                        ReciterEvents.AddReciter(
                            audio.recitation.reciter!!
                        )
                    )
                }
            ) {
                viewModel.fetchData()
            }

        }
    }

}

