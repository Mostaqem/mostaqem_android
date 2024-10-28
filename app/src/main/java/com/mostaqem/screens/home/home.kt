package com.mostaqem.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.screens.home.viewmodel.HomeViewModel
import com.mostaqem.screens.player.PlayerScreen

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {


    val scrollState = rememberScrollState()
    val surahState = viewModel.surahState.value
    val reciterState = viewModel.reciterState.value
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    );

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
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search"
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
                        Column(
                            verticalArrangement = Arrangement.Center,

                            ) {
                            AsyncImage(model = surah.image,
                                contentDescription = "avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .height(170.dp)
                                    .width(140.dp)
                                    .clickable {
                                        viewModel.playerState.value =
                                            viewModel.playerState.value.copy(surah = surah)
                                        viewModel.fetchMediaUrl()

                                    })
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = surah.arabicName,
                                textAlign = TextAlign.Center
                            )


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
                    contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp)
                ) {

                    items(reciterState.reciters) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {

                            AsyncImage(model = it.image,
                                contentDescription = "reciter",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .height(125.dp)
                                    .width(140.dp)
                                    .clickable {
                                        viewModel.playerState.value =
                                            viewModel.playerState.value.copy(reciter = it)
                                        viewModel.fetchMediaUrl()

                                    })
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = it.arabicName,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(100.dp)


                            )
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
                    ModalBottomSheet(

                        onDismissRequest = { viewModel.isPlayerVisible.value = false },
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
                            onProgressCallback = {},
                            progress = 0f,
                            playerIcon = viewModel.playerIcon.value
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = viewModel.playerState.value.surah?.image,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .size(65.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            viewModel.playerState.value.surah?.let {
                                Text(
                                    text = it.arabicName,
                                    fontWeight = FontWeight.W800,
                                    fontSize = 19.sp
                                )
                            }
                            Text(
                                text = viewModel.playerState.value.reciter.arabicName,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)

                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "play",
                                tint = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                                contentDescription = "play",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                    }

                }
            }

        }
    }

}
