package com.mostaqem.screens.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.R

import com.mostaqem.screens.home.presentation.components.ReciterCard
import com.mostaqem.screens.home.presentation.components.SurahCard
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.core.ui.theme.kufamFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(playerViewModel: PlayerViewModel, viewModel: HomeViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val surahs by viewModel.savedSurahs.collectAsState()
    val reciters by viewModel.savedReciters.collectAsState()
    val loading by viewModel.loading
    Column(Modifier.verticalScroll(scrollState)) {
        LargeTopAppBar(title = {
            Text(
                text = "السلام عليكم",
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = kufamFontFamily)
            )
        })
        Column(

        ) {

            if (surahs.isEmpty() && reciters.isEmpty() && !loading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 70.dp, end = 40.dp, start = 40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_box),
                        contentDescription = "empty",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.size(160.dp)
                    )
                    Text(
                        text = "استمع واختر قارئك المفضل لتبدأ هنا",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = kufamFontFamily,
                            fontWeight = FontWeight.W500
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
                            playerViewModel.playerState.value =
                                playerViewModel.playerState.value.copy(surah = surah)
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
                                playerViewModel.playerState.value =
                                    playerViewModel.playerState.value.copy(reciter = it)
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
