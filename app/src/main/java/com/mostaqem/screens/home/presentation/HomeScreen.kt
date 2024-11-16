package com.mostaqem.screens.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.screens.home.presentation.components.ReciterCard
import com.mostaqem.screens.home.presentation.components.SurahCard
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.ui.theme.kufamFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(playerViewModel: PlayerViewModel, viewModel: HomeViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val surahState = viewModel.savedSurahs.collectAsState()
    val reciterState = viewModel.savedReciters.collectAsState()
    Column(Modifier.verticalScroll(scrollState)) {
        LargeTopAppBar(title = {
            Text(
                text = "السلام عليكم",
                style = MaterialTheme.typography.headlineLarge
            )
        })
        Column(
            modifier = Modifier
                .statusBarsPadding()

        ) {

            Text(
                text = "سمع مؤخرا",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    horizontal = 30.dp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp)
            ) {
                items(surahState.value) { surah ->
                    SurahCard(image = surah.image, name = surah.arabicName) {
                        playerViewModel.playerState.value =
                            playerViewModel.playerState.value.copy(surah = surah)
                        playerViewModel.fetchMediaUrl()

                    }
                }


            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "شيوخ مختارة",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    horizontal = 30.dp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp),
                ) {
                    items(reciterState.value) {
                        ReciterCard(
                            image = it.image,
                            name = it.arabicName,

                            ) {
                            playerViewModel.playerState.value =
                                playerViewModel.playerState.value.copy(reciter = it)
                            playerViewModel.fetchMediaUrl()
                        }

                    }
                }

            }
            Spacer(modifier = Modifier.height(100.dp))


        }

    }


}
