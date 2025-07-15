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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.DownloadDestination
import com.mostaqem.dataStore
import com.mostaqem.features.offline.data.DownloadedAudioEntity
import com.mostaqem.features.settings.data.AppSettings
import java.text.DecimalFormat

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OfflineSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OfflineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val playOption =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.playDownloaded
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFile: DownloadedAudioEntity? by remember { mutableStateOf(null) }

    val downloads by viewModel.downloadedAudios.collectAsState()

    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        LargeFlexibleTopAppBar(
            title = {
                Text(
                    stringResource(R.string.offline),
                    fontFamily = MaterialTheme.typography.labelLarge.fontFamily
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                }
            },
        )


        ListItem(
            headlineContent = { Text(stringResource(R.string.automatically_play_offline)) },
            supportingContent = {
                Text(stringResource(R.string.automatically_play_offline_details))
            },
            trailingContent = {
                Switch(checked = playOption, thumbContent = {
                    if (playOption) Icon(
                        Icons.Default.Check, contentDescription = "check", Modifier.size(15.dp)
                    )
                }, onCheckedChange = {
                    viewModel.changePlayOption(it)
                })
            })

        ListItem(
            headlineContent = {
                Text(stringResource(R.string.downloads_title))
            }, supportingContent = {
                Text(stringResource(R.string.downloads_description))
            },
            modifier = Modifier.clickable {
                navController.navigate(DownloadDestination)
            }

        )

        ListItem(headlineContent = {
            Text(stringResource(R.string.storage))
        }, supportingContent = {
            Text(stringResource(R.string.storage_downloads))
        })


        LinearWavyProgressIndicator(
            progress = { viewModel.getMemoryPercentage() },

            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(8.dp)
        )


        LazyColumn {
            items(downloads) {
                ListItem(
                    headlineContent = { Text(it.title) },
                    supportingContent = {
                        Text(it.reciter)
                    }, trailingContent = {
                        Text(
                            it.size.toFileSizeString(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }, modifier = Modifier.clickable {
                        showDeleteDialog = true
                        selectedFile = it
                    })
            }
        }

        Spacer(Modifier.height(100.dp))

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
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight()
                ) {
                    Column(modifier) {
                        selectedFile?.title?.let { Text(it) }
                        selectedFile?.reciter?.let { Text(it) }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(
                                stringResource(R.string.cancel),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        TextButton(onClick = {
                            viewModel.delete(selectedFile!!.id)
                            showDeleteDialog = false

                        }) {
                            Text(
                                stringResource(R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Long.toFileSizeString(): String {
    val df = DecimalFormat("#.##") // For formatting to two decimal places

    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1.0 -> df.format(gb) + " GB"
        mb >= 1.0 -> df.format(mb) + " MB"
        kb >= 1.0 -> df.format(kb) + " KB"
        else -> "$this Bytes" // For sizes less than 1 KB
    }
}