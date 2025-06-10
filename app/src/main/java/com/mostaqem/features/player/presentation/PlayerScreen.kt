package com.mostaqem.features.player.presentation

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.features.player.data.BottomSheetType
import com.mostaqem.features.player.domain.CustomShape
import com.mostaqem.features.player.domain.MaterialShapes
import com.mostaqem.features.player.domain.Octagon
import com.mostaqem.features.player.presentation.components.PlayButtons
import com.mostaqem.features.player.presentation.components.PlayOptions
import com.mostaqem.features.player.presentation.components.QueuePlaylist
import com.mostaqem.features.player.presentation.components.sleep.SleepViewModel
import com.mostaqem.features.reciters.presentation.ReciterScreen
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.reciters.presentation.recitations.RecitationList
import com.mostaqem.features.settings.data.AppSettings
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    sleepViewModel: SleepViewModel,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    surahId: Int? = null,
    recitationID: Int? = null,
    onBack: () -> Unit,
    onShowBar: () -> Unit,
) {
    BackHandler {
        navController.popBackStack()
        onBack()
    }
    LaunchedEffect(Unit) {
        if (surahId != null && recitationID != null) playerViewModel.fetchMediaUrl(
            surahId = surahId.toInt(),
            recID = recitationID.toInt()
        )
        onShowBar()
        val isCached = playerViewModel.isCached
        val player = playerViewModel.playerState.value
        if (isCached && player.isLocal) {
            playerViewModel.applyCachedState()
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val playerSurah = playerViewModel.playerState.value
    val reciterViewModel: ReciterViewModel = hiltViewModel()
    val bottomSheetType by playerViewModel.currentBottomSheet.collectAsState()
    val percentage by playerViewModel.positionPercentage.collectAsState()
    val progress by playerViewModel.currentPosition.collectAsState()
    val duration by playerViewModel.duration.collectAsState()
    val customShapeData =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value
    var offset by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .offset { IntOffset(0, offset.roundToInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(onVerticalDrag = { _, dragAmount ->
                    offset += dragAmount
                }, onDragEnd = {
                    if (offset > dismissThreshold) {
                        onBack()
                        navController.popBackStack()
                    } else {
                        offset = 0f
                    }
                })
            }) {
        val orientation = LocalConfiguration.current.orientation
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),

                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        with(sharedTransitionScope) {
                            AsyncImage(
                                model = playerSurah.surah?.image,
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "image"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .clip(
                                        MaterialShapes.entries.find { it.id == customShapeData.shapeID }?.shape
                                            ?: MaterialShapes.RECT.shape
                                    )
                                    .size(300.dp)
                            )
                        }
                        Spacer(Modifier.width(30.dp))
                        VerticalDivider()
                        Spacer(Modifier.width(30.dp))

                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = playerSurah.surah!!.arabicName,
                                    fontSize = 30.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = playerSurah.reciter.arabicName)
                                Slider(
                                    value = percentage,
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

                                PlayButtons(playerViewModel = playerViewModel)
                            }
                            Column {
                                if (bottomSheetType != BottomSheetType.None) {
                                    ModalBottomSheet(
                                        sheetState = bottomSheetState,
                                        dragHandle = {},
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        onDismissRequest = {
                                            playerViewModel.hideBottomSheet()
                                        }) {
                                        when (bottomSheetType) {
                                            BottomSheetType.Queue -> QueuePlaylist(
                                                playlists = playerViewModel.queuePlaylist,
                                                playerViewModel = playerViewModel
                                            )

                                            BottomSheetType.Reciters -> ReciterScreen(
                                                playerViewModel = playerViewModel
                                            )

                                            BottomSheetType.None -> Box {}
                                            BottomSheetType.Recitations -> RecitationList(
                                                viewModel = reciterViewModel,
                                                playerViewModel = playerViewModel
                                            )
                                        }
                                    }
                                }
                                PlayOptions(
                                    playerViewModel = playerViewModel,
                                    viewModel = sleepViewModel,
                                    playerSurah = playerSurah,
                                    navController = navController
                                )
                            }
                        }
                    }

                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),

                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        BoxWithConstraints(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            val size = if (maxHeight <= 640.dp) 200.dp else 340.dp
                            AsyncImage(
                                model = playerSurah.surah?.image,
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clip(
                                        MaterialShapes.entries.find { it.id == customShapeData.shapeID }?.shape
                                            ?: MaterialShapes.RECT.shape
                                    )
                                    .size(size)
                            )
                        }
                        Column(modifier = Modifier.padding(20.dp)) {
                            playerSurah.surah?.let {
                                Text(
                                    text = it.arabicName,
                                    fontSize = 30.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = playerSurah.reciter.arabicName,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Slider(
                            value = percentage,
                            onValueChange = { playerViewModel.seekToPosition(it) },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = progress.toHoursMinutesSeconds(),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = duration.toHoursMinutesSeconds(),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        PlayButtons(playerViewModel = playerViewModel)
                    }
                    if (bottomSheetType != BottomSheetType.None) {
                        ModalBottomSheet(
                            sheetState = bottomSheetState,

                            containerColor = MaterialTheme.colorScheme.surface,
                            onDismissRequest = {
                                playerViewModel.hideBottomSheet()
                            }) {
                            when (bottomSheetType) {
                                BottomSheetType.Queue -> QueuePlaylist(
                                    playlists = playerViewModel.queuePlaylist,
                                    playerViewModel = playerViewModel
                                )

                                BottomSheetType.Reciters -> ReciterScreen(
                                    playerViewModel = playerViewModel
                                )

                                BottomSheetType.None -> Box {}
                                BottomSheetType.Recitations -> RecitationList(
                                    viewModel = reciterViewModel, playerViewModel = playerViewModel
                                )
                            }
                        }
                    }
                    PlayOptions(
                        playerViewModel = playerViewModel,
                        viewModel = sleepViewModel,
                        playerSurah = playerSurah,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    locale = "ar",
    device = "spec:width=360dp,height=640dp",
    widthDp = 340,
    heightDp = 640
)
@Composable
private fun Shape() {

    Column(
        modifier = Modifier.fillMaxSize(),

        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(vertical = 40.dp)) {

            Box(
                modifier = Modifier
                    .clip(
                        CustomShape(
                            shapeType = Octagon()
                        )
                    )
                    .background(Color.Green)
                    .size(200.dp)
                    .aspectRatio(1f)
            )




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
                value = 0.5f, onValueChange = { },

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
            Spacer(Modifier.height(50.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.outline_skip_next_24),
                    contentDescription = "previous",
                    modifier = Modifier.clickable {})
                Spacer(modifier = Modifier.width(16.dp))
                FilledIconButton(
                    onClick = {}, modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)

                ) {
                    Icon(
                        Icons.Default.PlayArrow, contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.outline_skip_previous_24),
                    contentDescription = "next",
                    modifier = Modifier.clickable { })

            }

        }
        Row(
            Modifier
                .padding(10.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_playlist_play_24),
                    contentDescription = "playlist"
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Person, contentDescription = "reciter")
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.baseline_radio_button_checked_24),
                    contentDescription = "recitation"
                )
            }

        }

    }


}