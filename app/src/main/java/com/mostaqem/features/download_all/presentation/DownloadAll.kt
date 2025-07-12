package com.mostaqem.features.download_all.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DiscFull
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.features.download_all.presentation.components.DownloadingScreen
import com.mostaqem.features.offline.presentation.OfflineViewModel
import com.mostaqem.features.personalization.presentation.components.LargeTopBar
import com.mostaqem.features.personalization.presentation.reciter.ReciterOption
import com.mostaqem.features.player.presentation.PlayerViewModel
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DownloadAlLScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    viewModel: OfflineViewModel = hiltViewModel()
) {
    val state by viewModel.downloadUiState.collectAsState()
    val downloadedCount = state.values.size
    val completedDownloadsCount = state.values.filter { it.state == Download.STATE_COMPLETED }.size
    val isPaused by viewModel.isPaused.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDownloadAllDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val loading by viewModel.isLoading.collectAsState()

    if (showDeleteDialog) {
        Dialog(onDismissRequest = {
            showDeleteDialog = false
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),

                ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        stringResource(R.string.delete_all_downloads_question),
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        stringResource(R.string.delete_all_downloads_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(10.dp))

                    Row {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text(
                                    stringResource(R.string.cancel),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            TextButton(onClick = {
                                viewModel.removeALl()
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

    if (showDownloadAllDialog) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showDownloadAllDialog = false
            }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    stringResource(R.string.download_all_chapters_question),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    stringResource(R.string.download_all_chapters_description),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
                ReciterOption(playerViewModel = playerViewModel)

                Spacer(Modifier.height(20.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showDownloadAllDialog = false
                                }
                            }
                        },
                        modifier = Modifier.size(width = 150.dp, height = 80.dp),
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                        )
                    }
                    Spacer(Modifier.width(20.dp))

                    ElevatedButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.size(width = 150.dp, height = 80.dp),
                        onClick = {
                            viewModel.downloadAll()

                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showDownloadAllDialog = false
                                }
                            }


                        }) {
                        Text(
                            stringResource(R.string.download),
                        )
                    }


                }
            }

        }
    }


    Column(modifier = modifier) {
        LargeTopBar(
            navController = navController,
            title = stringResource(R.string.downloads_title),
            actions = {
                IconButton(onClick = {
                    showDownloadAllDialog = true
                }) {
                    Icon(
                        Icons.Outlined.CloudDownload,
                        contentDescription = "download_all",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                AnimatedVisibility(visible = downloadedCount != 0) {
                    IconButton(onClick = {
                        showDeleteDialog = true
                    }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

            })

        Spacer(Modifier.height(20.dp))
        if (loading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularWavyProgressIndicator()

            }
        } else {
            if (downloadedCount != 0) {
                DownloadingScreen(
                    downloadedCount = downloadedCount,
                    downloads = state.values.toMutableList(),
                    completedCount = completedDownloadsCount,
                    isDownloading = isDownloading,
                    isPaused = isPaused,
                    onPause = { viewModel.pauseDownload() },
                    onCancel = { viewModel.removeDownload(it) },
                    onResume = { viewModel.resumeDownload() }
                )
            } else {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.DiscFull,
                        contentDescription = "sd",
                        Modifier.size(200.dp)
                    )
                    Text(stringResource(R.string.no_downloads), style = MaterialTheme.typography.titleLarge)

                }


            }

        }


    }


}

