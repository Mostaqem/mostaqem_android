package com.mostaqem.screens.player.presentation

import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.screens.player.data.BottomSheetType
import com.mostaqem.screens.player.domain.CustomShape
import com.mostaqem.screens.player.domain.MaterialShapes
import com.mostaqem.screens.player.domain.Octagon
import com.mostaqem.screens.player.presentation.components.QueuePlaylist
import com.mostaqem.screens.reciters.presentation.Recitations.RecitationList
import com.mostaqem.screens.reciters.presentation.ReciterScreen
import com.mostaqem.screens.reciters.presentation.ReciterViewModel
import com.mostaqem.screens.settings.domain.AppSettings
import com.mostaqem.utils.LockScreenOrientation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    progressPercentage: Float,
    progress: Float,
    playerIcon: Int,
    duration: Float,
    playerViewModel: PlayerViewModel

) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val playerSurah = playerViewModel.playerState.value
    val reciterViewModel: ReciterViewModel = hiltViewModel()
    val bottomSheetType by playerViewModel.currentBottomSheet.collectAsState()
    val isPlaying: Boolean = playerIcon == R.drawable.outline_pause_24
    val animatedWidth by animateIntAsState(
        targetValue = if (isPlaying) 120 else 70, label = "animatedWidth", animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow

        )
    )
    val customShapeData =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            BoxWithConstraints(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                AsyncImage(
                    model = playerSurah.surah?.image,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(
                            MaterialShapes.entries.find { it.id == customShapeData.shapeID }?.shape
                                ?: MaterialShapes.RECT.shape
                        )
                        .size(if (maxHeight > 620.dp) 300.dp else 170.dp)


                )
            }


            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = playerSurah.surah!!.arabicName,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(text = playerSurah.reciter.arabicName)
            }
            Slider(
                value = progressPercentage,
                onValueChange = { playerViewModel.seekToPosition(it) },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = progress.toHoursMinutesSeconds())
                Text(text = duration.toHoursMinutesSeconds())
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Icon(painter = painterResource(id = R.drawable.outline_skip_next_24),
                    contentDescription = "previous",
                    modifier = Modifier.clickable {
                        playerViewModel.seekPrevious()
                    })
                Spacer(modifier = Modifier.width(16.dp))
                FilledIconButton(
                    onClick = {
                        playerViewModel.handlePlayPause()
                    }, modifier = Modifier
                        .width(animatedWidth.dp)
                        .height(70.dp)
                ) {
                    Icon(
                        painter = painterResource(id = playerIcon), contentDescription = "playPause"
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Icon(painter = painterResource(id = R.drawable.outline_skip_previous_24),
                    contentDescription = "next",
                    modifier = Modifier.clickable { playerViewModel.seekNext() })

            }
        }
        if (bottomSheetType != BottomSheetType.None) {
            ModalBottomSheet(sheetState = bottomSheetState,
                dragHandle = {},
                containerColor = MaterialTheme.colorScheme.surface,
                onDismissRequest = {
                    playerViewModel.hideBottomSheet()
                }) {
                when (bottomSheetType) {
                    BottomSheetType.Queue -> QueuePlaylist(
                        playlists = playerViewModel.queuePlaylist, player = playerSurah
                    )

                    BottomSheetType.Reciters -> ReciterScreen(viewModel = reciterViewModel)
                    BottomSheetType.None -> Box {}
                    BottomSheetType.Recitations -> RecitationList(
                        viewModel = reciterViewModel, playerViewModel = playerViewModel
                    )
                }
            }
        }


        Row(
            Modifier
                .padding(10.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                playerViewModel.showBottomSheet(BottomSheetType.Queue)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_playlist_play_24),
                    contentDescription = "playlist"
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {
                playerViewModel.showBottomSheet(BottomSheetType.Reciters)
            }) {
                Icon(Icons.Outlined.Person, contentDescription = "reciter")
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {
                playerViewModel.showBottomSheet(BottomSheetType.Recitations)
            }) {
                Icon(
                    painter = painterResource(R.drawable.outline_spa_24),
                    contentDescription = "recitation"
                )
            }

        }
    }


}

@Preview(showBackground = true, locale = "ar", device = "spec:width=360dp,height=640dp")
@Composable
private fun Shape() {

    Column(
        modifier = Modifier
            .fillMaxSize(),

        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(vertical = 40.dp)) {
            BoxWithConstraints(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(
                    modifier = Modifier
                        .clip(
                            CustomShape(
                                shapeType = Octagon()
                            )
                        )
                        .background(Color.LightGray)
                        .size(if (maxHeight > 620.dp) 300.dp else 170.dp)
                )
            }

            Column(modifier = Modifier.padding(vertical = 45.dp, horizontal = 25.dp)) {
                Text(
                    text = "الفاتحة",
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "عبدالباسط عبدالصمد")
            }
            Slider(
                value = 0.5f,
                onValueChange = { },

                modifier = Modifier.padding(horizontal = 25.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = "0.5")
                Text(text = "0.5")
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Icon(painter = painterResource(id = R.drawable.outline_skip_next_24),
                    contentDescription = "previous",
                    modifier = Modifier.clickable {
                    })
                Spacer(modifier = Modifier.width(16.dp))
                FilledIconButton(
                    onClick = {
                    }, modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)

                ) {
                    Icon(
                        Icons.Default.PlayArrow, contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Icon(painter = painterResource(id = R.drawable.outline_skip_previous_24),
                    contentDescription = "next",
                    modifier = Modifier.clickable { })

            }

        }
        Row(
            Modifier
                .padding(10.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_playlist_play_24),
                    contentDescription = "playlist"
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {
            }) {
                Icon(Icons.Outlined.Person, contentDescription = "reciter")
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {
            }) {
                Icon(
                    painter = painterResource(R.drawable.outline_spa_24),
                    contentDescription = "recitation"
                )
            }

        }

    }


}