package com.mostaqem.features.player.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.mostaqem.R
import com.mostaqem.features.player.presentation.PlayerViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayButtons(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    isArabic: Boolean
) {
    val playerIcon by playerViewModel.playPauseIcon.collectAsState()
    val isPlaying: Boolean = playerIcon == R.drawable.outline_pause_24
    val animatedWidth by animateIntAsState(
        targetValue = if (isPlaying) 150 else 90, label = "animatedWidth", animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow

        )
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = "previous",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(15.dp)
                    .clickable {
                        if (isArabic) playerViewModel.seekPrevious() else playerViewModel.seekNext()

                    })
            Spacer(modifier = Modifier.width(16.dp))

            FilledIconButton(
                onClick = {
                    playerViewModel.handlePlayPause()
                }, modifier = Modifier
                    .width(animatedWidth.dp)
                    .height(90.dp)
            ) {
                Icon(
                    painter = painterResource(id = playerIcon), contentDescription = "playPause"
                )
            }


            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = "next",
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(15.dp)
                    .clickable {
                        if (isArabic) playerViewModel.seekNext() else playerViewModel.seekPrevious()

                    })
        }
    }
}
