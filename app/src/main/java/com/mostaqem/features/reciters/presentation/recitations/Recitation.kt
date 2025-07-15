package com.mostaqem.features.reciters.presentation.recitations

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.features.language.presentation.LanguageViewModel
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings

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
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val isArabic = languageCode == "ar"
    
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Text(
                text = stringResource(R.string.recitations),
                modifier = modifier.padding(horizontal = 12.dp, vertical = 15.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(recitations) {
            ListItem(headlineContent = {},
                leadingContent = { Text(text = if (isArabic) it.name else it.englishName
                    ?: it.name, modifier = Modifier.width(width = 200.dp)) },
                modifier = Modifier.clickable {
                    playerViewModel.changeRecitation(it.id)
                },
                colors = if (it.id == currentRecitationID) ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else ListItemDefaults.colors(),
                trailingContent = {
                    RadioButton(
                        selected = it.id == currentRecitationID,
                        onClick = {
                            playerViewModel.changeRecitation(it.id)
                        })
                }
            )
        }
    }
}