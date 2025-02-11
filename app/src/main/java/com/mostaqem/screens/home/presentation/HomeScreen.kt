package com.mostaqem.screens.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.core.network.NetworkConnectivityObserver
import com.mostaqem.core.network.models.NetworkResult
import com.mostaqem.core.network.models.NetworkStatus

import com.mostaqem.screens.home.presentation.components.ReciterCard
import com.mostaqem.screens.home.presentation.components.SurahCard
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.screens.player.domain.MaterialShapes
import com.mostaqem.screens.reciters.domain.ReciterEvents
import com.mostaqem.screens.reciters.presentation.ReciterViewModel
import com.mostaqem.screens.surahs.data.AudioData
import com.mostaqem.screens.surahs.presentation.SurahsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel,
    viewModel: HomeViewModel = hiltViewModel(),
    surahViewModel: SurahsViewModel = hiltViewModel(),
    reciterViewModel: ReciterViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val surahs by viewModel.savedSurahs.collectAsState()
    val reciters by viewModel.savedReciters.collectAsState()
    val loading by viewModel.loading
    val randomSurahs by viewModel.randomSurah.collectAsState()

    Column(Modifier.verticalScroll(scrollState)) {
        LargeTopAppBar(title = {
            Text(
                text = "السلام عليكم",
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = kufamFontFamily)
            )
        })
        Spacer(modifier = Modifier.height(24.dp))

        Column {
            if (!loading) {
                Text(
                    "مرشح لك",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (randomSurahs) {
                        is NetworkResult.Error -> {
                            items(3) {
                                Box(
                                    modifier = Modifier
                                        .height(100.dp)
                                        .width(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .clickable {
                                            viewModel.fetchData()
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

                        is NetworkResult.Loading -> {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillParentMaxWidth()
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        is NetworkResult.Success -> {
                            val data =
                                (randomSurahs as NetworkResult.Success<List<AudioData>>).data
                            items(data) {
                                Box(contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .height(100.dp)
                                        .width(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .clickable {
                                            playerViewModel.playAudioData(it)
                                            surahViewModel.onSurahEvents(SurahEvents.AddSurah(it.surah))
                                            reciterViewModel.onReciterEvents(
                                                ReciterEvents.AddReciter(
                                                    it.recitation.reciter
                                                )
                                            )
                                        }

                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text(
                                                it.surah.arabicName,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Text(
                                                it.recitation.reciter.arabicName,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        Box(
                                            contentAlignment = Alignment.Center,
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
                        text = "استمع واختر قارئك المفضل لتبدأ هنا",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = kufamFontFamily, fontWeight = FontWeight.W500
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
                    CircularProgressIndicator()
                }
            }
            if (surahs.isNotEmpty()) {
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
                    items(surahs) { surah ->
                        SurahCard(image = surah.image, name = surah.arabicName) {
                            playerViewModel.changeSurah(surah)
                            playerViewModel.fetchMediaUrl()

                        }
                    }


                }
            }


            Spacer(modifier = Modifier.height(24.dp))
            if (reciters.isNotEmpty()) {
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
                        items(reciters) {
                            ReciterCard(
                                image = it.image,
                                name = it.arabicName,

                                ) {
                                playerViewModel.changeReciter(it)
                                playerViewModel.fetchMediaUrl()
                            }

                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(100.dp))


        }

    }


}
