package com.mostaqem.features.player.presentation.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mostaqem.core.navigation.models.Destination
import com.mostaqem.core.ui.app.Navigator
import com.mostaqem.features.player.data.PlayerSurah
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerBarModalSheet(
    navigator: Navigator,
    playerState: PlayerSurah,
    positionPercentage: State<Float>,
    playPauseIcon: State<Int>,
    onPlayPause: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,

    ) {
    val surah = playerState.surah
    val reciter = playerState.reciter
    var offset by remember { mutableFloatStateOf(0f) }

    val dismissThreshold = 100
    if (surah != null) {
        Box(
            modifier = modifier
                .navigationBarsPadding()
                .offset { IntOffset(0, offset.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            navigator.navigate(Destination.PlayerDestination)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            offset += dragAmount
                        },
                        onDragEnd = {
                            if (offset > dismissThreshold) {
                                onClear()
                            } else {
                                offset = 0f
                                navigator.navigate(Destination.PlayerDestination)
                            }
                        }
                    )
                }
                .padding(horizontal = 10.dp, vertical = 12.dp)

                .clip(RoundedCornerShape(16.dp))

                .background(MaterialTheme.colorScheme.surfaceContainer)

                .height(80.dp)

                .fillMaxWidth()

        ) {
            PlayerBar(
                playerIcon = playPauseIcon.value,
                onPlayPause = onPlayPause,
                surahName = surah.arabicName,
                reciterName = reciter.arabicName,
                progress = positionPercentage.value,
            )
        }
    }

}
