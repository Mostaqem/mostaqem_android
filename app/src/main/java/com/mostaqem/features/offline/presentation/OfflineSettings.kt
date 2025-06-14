package com.mostaqem.features.offline.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.data.AudioData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OfflineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val playOption =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.playDownloaded
    val downloads by viewModel.downloaded.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFile: AudioData? by remember { mutableStateOf(null) }
    val languageCode =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.offline), fontFamily = fontFamily)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.automatically_play_offline)) },
                supportingContent = {
                    Text(stringResource(R.string.automatically_play_offline_details))
                },
                trailingContent = {
                    Switch(checked = playOption, onCheckedChange = {
                        viewModel.changePlayOption(it)
                    })
                })
        }
        item {
            ListItem(
                headlineContent = {
                    Text(stringResource(R.string.storage))
                },
                supportingContent = {
                    Text(stringResource(R.string.storage_downloads))
                }
            )
        }
        item {
            LinearProgressIndicator(
                progress = { viewModel.getMemoryPercentage() },

                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(8.dp)
            )
        }

        items(downloads) {
            ListItem(
                headlineContent = { Text(it.surah.arabicName) },
                leadingContent = {
                    AsyncImage(
                        model = it.surah.image,
                        contentDescription = "surah",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                },
                supportingContent = {
                    Text(it.recitation.reciter.arabicName)
                },
                trailingContent = {
                    Text(
                        (it.size / 1000).toString() + " " + stringResource(R.string.kilo_byte),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                },
                modifier = Modifier.clickable {
                    showDeleteDialog = true
                    selectedFile = it
                }
            )
        }
        item {
            Spacer(Modifier.height(100.dp))

        }

    }
    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight()) {
                    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier) {
                            selectedFile?.surah?.let { Text(it.arabicName) }
                            selectedFile?.recitation?.reciter?.let { Text(it.arabicName) }
                        }
                        AsyncImage(
                            model = selectedFile!!.surah.image,
                            contentDescription = "surah",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.secondary)
                        }
                        TextButton(onClick = {
                            viewModel.delete(selectedFile!!.url)
                            showDeleteDialog = false

                        }) {
                            Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}