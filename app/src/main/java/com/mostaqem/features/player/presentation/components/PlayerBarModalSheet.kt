package com.mostaqem.features.player.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mostaqem.core.navigation.models.PlayerDestination
import com.mostaqem.features.player.presentation.PlayerViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerBarModalSheet(
    modifier: Modifier = Modifier,
    hidePlayer: MutableState<Boolean>,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val playerIcon by playerViewModel.playPauseIcon.collectAsState()
    val surah = playerViewModel.playerState.value.surah
    val reciter = playerViewModel.playerState.value.reciter
    val progress by playerViewModel.positionPercentage.collectAsState()
    var offset by remember { mutableFloatStateOf(0f) }

    val dismissThreshold = 100
    Box(modifier = modifier
            .offset { IntOffset(0, offset.roundToInt()) }
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    hidePlayer.value = true
                    navController.navigate(PlayerDestination)

                }
            )
        }
        .pointerInput(Unit) {
            // Handle drag gestures
            detectVerticalDragGestures(
                onVerticalDrag = { _, dragAmount ->
                    offset += dragAmount
                },
                onDragEnd = {
                    if (offset > dismissThreshold) {
                        playerViewModel.clear()
                    } else {
                        hidePlayer.value = true
                        navController.navigate(PlayerDestination)
                        offset = 0f
                    }
                }
            )
        }
        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        .height(80.dp)
        .fillMaxWidth()

    ) {
        PlayerBar(
            image = surah!!.image,
            playerIcon = playerIcon,
            onPlayPause = { playerViewModel.handlePlayPause() },
            surahName = surah.arabicName ,
            reciterName = reciter.arabicName,
            progress = progress,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )

    }
}
