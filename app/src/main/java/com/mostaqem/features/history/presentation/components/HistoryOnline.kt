package com.mostaqem.features.history.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.network.models.Result
import com.mostaqem.features.history.data.HistoryState
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.AudioData
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.presentation.components.SurahOptions

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
fun HistoryOnline(
    playerViewModel: PlayerViewModel,
    isArabic: Boolean,
    fontFamily: FontFamily,
    state: HistoryState,
    onCardClick: (Surah) -> Unit,
    onAddSurah: (AudioData) -> Unit,
    onFetchData: () -> Unit
) {
    val surahs = state.savedSurahs
    val reciters = state.savedReciters
    val loading = state.loading
    val randomSurahs = state.randomSurah

    Column {


        if (!loading) {
            Text(
                stringResource(R.string.recommended),
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = fontFamily,
                modifier = Modifier.padding(horizontal = 30.dp),

                )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (randomSurahs) {
                    is Result.Error -> {
                        items(3) {
                            Box(
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .clickable {
                                        onFetchData()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "refresh",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }


                        }

                    }

                    is Result.Loading -> {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillParentMaxWidth()
                            ) {
                                LoadingIndicator()
                            }
                        }
                    }

                    is Result.Success -> {
                        items(randomSurahs.data) { audio ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onLongPress = {
                                            onCardClick(audio.surah!!)
                                        }, onTap = {
                                            onAddSurah(audio)
                                        })

                                    }

                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            if (!isArabic) audio.surah.complexName else audio.surah.arabicName,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontFamily = fontFamily,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )


                                    Text(
                                        if (!isArabic) audio.recitation.reciter.englishName else audio.recitation.reciter.arabicName,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.3f
                                            )
                                        )
                                        .size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        modifier = Modifier.size(20.dp),
                                        contentDescription = "sd"
                                    )
                                }


                            }


                        }
                    }
                }
            }

        }
    }



    Spacer(modifier = Modifier.height(24.dp))

    if (surahs.isEmpty() && reciters.isEmpty() && !loading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, end = 40.dp, start = 40.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.baseline_album_24),
                contentDescription = "empty",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            )
            Text(
                text = stringResource(R.string.listen_choose),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = fontFamily, fontWeight = FontWeight.W500
                )
            )
        }

    }
    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingIndicator()
        }
    }
    if (surahs.isNotEmpty()) {
        Text(
            text = stringResource(R.string.heard),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = fontFamily,
            modifier = Modifier.padding(
                horizontal = 30.dp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp)
        ) {
            items(surahs) { surah ->
                SurahCard(
                    image = surah.image,
                    name = if (isArabic) surah.arabicName else surah.complexName,
                    onClick = {
                        playerViewModel.changeSurah(surah)
                    }) {
                    onCardClick(surah)

                }
            }


        }
    }


    Spacer(modifier = Modifier.height(24.dp))
    if (reciters.isNotEmpty()) {
        Text(
            text = stringResource(R.string.selected_reciters),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = fontFamily,
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
                items(reciters) {
                    ReciterCard(
                        image = it.image,
                        name = if (isArabic) it.arabicName else it.englishName,
                    ) {
                        playerViewModel.changeReciter(it)
                    }

                }
            }

        }
    }
    Spacer(modifier = Modifier.height(100.dp))
}
}
