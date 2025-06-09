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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.history.presentation.components.HistoryOnline
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.domain.ReciterEvents
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.presentation.SurahsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HistoryScreen(
    playerViewModel: PlayerViewModel,
    viewModel: HistoryViewModel = hiltViewModel(),
    surahViewModel: SurahsViewModel = hiltViewModel(),
    reciterViewModel: ReciterViewModel = hiltViewModel(),
    navController: NavController,
    paddingValues: PaddingValues
) {
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn(contentPadding = paddingValues) {
        item {
            LargeTopAppBar(title = {
                Text(
                    text = stringResource(R.string.hello),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = fontFamily,
                    modifier = Modifier.padding(start = 12.dp)
                )
            })
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            HistoryOnline(
                playerViewModel,
                navController,
                languageCode,
                fontFamily,
                uiState,
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

