package com.mostaqem.features.reciters.presentation.recitations

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.features.language.presentation.LanguageViewModel
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.data.RecitationData
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RecitationList(
    modifier: Modifier = Modifier,
    viewModel: ReciterViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel,
    selectedReciter: Reciter,
    onClick: (RecitationData) -> Unit,
) {
    val player = playerViewModel.playerState.value
    viewModel.getRecitations(selectedReciter.id)
    val recitations by viewModel.recitationState.collectAsState()
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val isArabic = languageCode == "ar"
    val defaultRecitation by playerViewModel.defaultRecitation.collectAsState()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Text(
                text = stringResource(R.string.recitations),
                modifier = modifier.padding(horizontal = 12.dp, vertical = 15.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        items(recitations) {
            val isCurrentRecitation: Boolean =
                (player.recitationID ?: defaultRecitation.id) == it.id
            ListItem(
                headlineContent = {},
                leadingContent = {
                    Text(
                        text = if (isArabic) it.name else it.englishName
                            ?: it.name,
                        modifier = Modifier.width(200.dp),
                        style = MaterialTheme.typography.titleMediumEmphasized
                    )
                },
                modifier = Modifier.clickable {
                    onClick(it)
                },
                colors = if (isCurrentRecitation) ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer) else ListItemDefaults.colors(),
                trailingContent = {
                    RadioButton(
                        selected = isCurrentRecitation,
                        onClick = {
                            onClick(it)
                        })
                }
            )
        }
    }
}