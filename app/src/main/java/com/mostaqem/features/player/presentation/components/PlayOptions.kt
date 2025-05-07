package com.mostaqem.features.player.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mostaqem.R
import com.mostaqem.features.player.data.BottomSheetType
import com.mostaqem.features.player.presentation.PlayerViewModel

@Composable
fun PlayOptions(modifier: Modifier = Modifier, playerViewModel: PlayerViewModel) {
    var isDownloading by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = {
            playerViewModel.showBottomSheet(BottomSheetType.Queue)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.outline_playlist_play_24),
                contentDescription = "playlist",
            )
        }
        Spacer(modifier = Modifier.width(15.dp))

        IconButton(onClick = {
            playerViewModel.showBottomSheet(BottomSheetType.Reciters)
        }) {
            Icon(
                Icons.Outlined.Person, contentDescription = "reciter",

                )
        }
        Spacer(modifier = Modifier.width(15.dp))

        IconButton(onClick = {
            playerViewModel.showBottomSheet(BottomSheetType.Recitations)
        }) {
            Icon(
                painter = painterResource(R.drawable.baseline_radio_button_checked_24),
                contentDescription = "recitation",

                )
        }
        Spacer(modifier = Modifier.width(15.dp))
        AnimatedVisibility(
            visible = !playerViewModel.isCurrentSurahDownloaded()
        ) {
            IconButton(onClick = {
                if (!isDownloading) playerViewModel.download()
                isDownloading = true

            }) {
                Icon(
                    painter = painterResource(if (isDownloading) R.drawable.download_off else R.drawable.download),
                    contentDescription = "download",

                    )
            }
        }

    }
}