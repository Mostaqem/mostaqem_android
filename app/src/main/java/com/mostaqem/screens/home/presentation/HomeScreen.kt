package com.mostaqem.screens.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.screens.home.presentation.components.PlayerBar
import com.mostaqem.screens.home.presentation.components.ReciterCard
import com.mostaqem.screens.home.presentation.components.SurahCard
import com.mostaqem.screens.home.presentation.player.PlayerScreen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val surahState = viewModel.surahState.value
    val reciterState = viewModel.reciterState.value
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val playerIcon by viewModel.playPauseIcon.collectAsState()
    val positionPercentage by viewModel.positionPercentage.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()


    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .verticalScroll(scrollState)
                .statusBarsPadding()

        ) {

            DockedSearchBar(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
                query = "",
                onQueryChange = {},
                placeholder = { Text(text = "البحث عن ...") },
                onSearch = {},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search, contentDescription = "search"
                    )
                },
                active = false,
                onActiveChange = {}) {

            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "سمع مؤخرا",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(
                    horizontal = 30.dp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp)
                ) {
                    items(surahState.surahs) { surah ->
                        SurahCard(image = surah.image, name = surah.arabicName) {
                            viewModel.playerState.value =
                                viewModel.playerState.value.copy(surah = surah)
                            viewModel.fetchMediaUrl()

                        }
                    }

                }
                if (surahState.isLoading) CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "شيوخ مختارة",
                style = MaterialTheme.typography.displayLarge,
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
                    items(reciterState.reciters) {
                        ReciterCard(
                            image = it.image,
                            name = it.arabicName,

                            ) {
                            viewModel.playerState.value =
                                viewModel.playerState.value.copy(reciter = it)
                            viewModel.fetchMediaUrl()
                        }

                    }
                }
                if (reciterState.isLoading) CircularProgressIndicator()

            }
            Spacer(modifier = Modifier.height(100.dp))


        }
        if (viewModel.playerState.value.surah != null) {
            Box(modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .height(100.dp)
                .clickable {
                    viewModel.isPlayerVisible.value = true

                }
                .fillMaxWidth()

            ) {
                if (viewModel.isPlayerVisible.value) {
                    ModalBottomSheet(onDismissRequest = { viewModel.isPlayerVisible.value = false },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.background,
                        dragHandle = {}) {
                        PlayerScreen(
                            sheetState = viewModel.isPlayerVisible,
                            playerSurah = viewModel.playerState.value,
                            onPlay = {
                                viewModel.handlePlayPause()
                            },
                            onSeekPrevious = {},
                            onSeekNext = {},
                            onProgressCallback = {
                                viewModel.seekToPosition(it)
                            },
                            progress = currentPosition.toFloat(),
                            playerIcon = playerIcon,
                            progressPercentage = positionPercentage,
                            duration = duration.toFloat()
                        )
                    }
                }
                PlayerBar(
                    image = viewModel.playerState.value.surah!!.image,
                    playerIcon = playerIcon,
                    onPlayPause = {
                        viewModel.handlePlayPause()
                    },
                    surahName = viewModel.playerState.value.surah!!.arabicName,
                    reciterName = viewModel.playerState.value.reciter.arabicName
                )

            }

        }
    }

}
