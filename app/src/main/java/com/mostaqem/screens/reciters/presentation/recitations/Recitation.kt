package com.mostaqem.screens.reciters.presentation.recitations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.reciters.presentation.ReciterViewModel

@Composable
fun RecitationList(
    modifier: Modifier = Modifier,
    viewModel: ReciterViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel
) {
    val player = playerViewModel.playerState.value

    val currentPlayingReciter: Int = player.reciter.id
    val currentRecitationID: Int = player.recitationID ?: 0
    viewModel.getRecitations(currentPlayingReciter)
    val recitations by viewModel.recitationState.collectAsState()

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Text(
                text = "تلاوات",
                modifier = modifier.padding(horizontal = 12.dp, vertical = 15.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(recitations) {
            ListItem(headlineContent = { },
                leadingContent = { Text(text = it.name) },
                modifier = Modifier.clickable {
                    playerViewModel.changeRecitation(it.id)
                    playerViewModel.fetchMediaUrl()
                },
                colors = if (it.id == currentRecitationID) ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else ListItemDefaults.colors(),
                trailingContent = {
                    RadioButton(
                        selected = it.id == currentRecitationID,
                        onClick = {})
                }
            )
        }
    }
}