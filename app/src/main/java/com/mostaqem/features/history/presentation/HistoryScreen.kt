package com.mostaqem.features.history.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
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
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.presentation.SurahsViewModel
import com.mostaqem.features.surahs.presentation.components.SurahOptions

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HistoryScreen(
    playerViewModel: PlayerViewModel,
    viewModel: HistoryViewModel = hiltViewModel(),
    surahViewModel: SurahsViewModel = hiltViewModel(),
    reciterViewModel: ReciterViewModel = hiltViewModel(),
    navController: NavController,
    paddingValues: PaddingValues,
    onHideBottom: (Boolean) -> Unit,
) {
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSurah: Surah? by remember {
        mutableStateOf(null)
    }
    var selectedReciter: Reciter? by remember { mutableStateOf(null) }
    var selectedRecitation: Int? by remember { mutableStateOf(null) }
    var openOptionsSheet by remember { mutableStateOf(false) }

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
            onSurahClick = {
                playerViewModel.changeSurah(it)
                playerViewModel.fetchMediaUrl()
                surahViewModel.onSurahEvents(SurahEvents.AddSurah(it))
            },
            onSelect = { viewModel.onFilterSelected(it) },
            onSurahMenuClick = {
                selectedSurah = it
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
                        selectedSurah = selectedSurah,
                        playerViewModel = playerViewModel,
                        navController = navController,
                        selectedReciter = selectedReciter,
                        isArabic = languageCode == "ar",
                        selectedRecitationID = selectedRecitation,
                    ) {
                        openOptionsSheet = false
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            HistoryOnline(
                playerViewModel = playerViewModel,
                isArabic = languageCode == "ar",
                fontFamily = fontFamily,
                state = uiState,
                onCardClick = {
                    selectedSurah = it
                    openOptionsSheet = true
                },
                onAddSurah = {
                    playerViewModel.playAudioData(it)
                    surahViewModel.onSurahEvents(
                        SurahEvents.AddSurah(
                            it.surah
                        )
                    )
                    reciterViewModel.onReciterEvents(
                        ReciterEvents.AddReciter(
                            it.recitation.reciter
                        )
                    )
                }
            ) {
                viewModel.fetchData()
            }

        }
    }

}

