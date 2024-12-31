package com.mostaqem.screens.player.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mostaqem.screens.player.data.BottomSheetType
import com.mostaqem.screens.player.presentation.PlayerScreen
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.surahs.data.Surah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerBarModalSheet(
    surah: Surah,
    isPlayerShown: Boolean,
    onShowPlayer: () -> Unit,
    onDismissPlayer: () -> Unit,
    playerViewModel: PlayerViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val playerIcon by playerViewModel.playPauseIcon.collectAsState()
    val positionPercentage by playerViewModel.positionPercentage.collectAsState()
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    val duration by playerViewModel.duration.collectAsState()

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .height(80.dp)
            .fillMaxWidth()
            .clickable { onShowPlayer() }
    ) {
        PlayerBar(
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp),
            image = surah.image,
            playerIcon = playerIcon,
            onPlayPause = { playerViewModel.handlePlayPause() },
            surahName = surah.arabicName,
            reciterName = playerViewModel.playerState.value.reciter.arabicName,
            progress = positionPercentage
        )
    }

    if (isPlayerShown) {
        ModalBottomSheet(
            onDismissRequest = onDismissPlayer,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            dragHandle = {}
        ) {
            PlayerScreen(
                progress = currentPosition.toFloat(),
                playerIcon = playerIcon,
                progressPercentage = positionPercentage,
                duration = duration.toFloat(),
                playerViewModel = playerViewModel
            )
        }
    }
}
