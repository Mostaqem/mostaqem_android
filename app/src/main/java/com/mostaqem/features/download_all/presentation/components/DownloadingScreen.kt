package com.mostaqem.features.download_all.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import com.mostaqem.R
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.features.offline.data.DownloadUiState
import kotlin.math.roundToInt

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun DownloadingScreen(
    modifier: Modifier = Modifier,
    downloadedCount: Int,
    completedCount: Int,
    downloads: List<DownloadUiState>,
    onPause: () -> Unit,
    isPaused: Boolean,
    isDownloading: Boolean,
    onResume: () -> Unit,
    onCancel: (String) -> Unit,
) {
    val progress: Float = if (downloadedCount != 0) {
        (completedCount.toFloat() / downloadedCount)
    } else {
        0f
    }
    var showText by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularWavyProgressIndicator(
                    progress = { progress },
                    amplitude = { if (isPaused || progress == 1f) 0f else 1f },
                    stroke = Stroke(width = 13f),
                    modifier = Modifier
                        .size(200.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(progress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        stringResource(R.string.downloaded),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
            }
        }
        item {

            if (isDownloading) {
                FilledIconButton(
                    onClick = { onPause() },
                    modifier = Modifier.size(width = 130.dp, height = 70.dp)
                ) {
                    Row {
                        Icon(
                            Icons.Outlined.Pause,
                            contentDescription = "pause"
                        )
                        Text(
                            stringResource(R.string.pause),
                            style = MaterialTheme.typography.titleMedium

                        )
                    }

                }
            } else {
                FilledTonalIconButton(
                    onClick = { onResume() },
                    modifier = Modifier.size(width = 130.dp, height = 70.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.PlayArrow,
                            contentDescription = "resume"
                        )
                        Text(
                            stringResource(R.string.resume),
                            style = MaterialTheme.typography.titleMedium
                        )

                    }

                }
            }

        }

        if (downloads.isEmpty()) {
            item {}
        } else {

            items(downloads, key = { t -> t.downloadID }) { state ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                            onCancel(state.downloadID)
                            true // Indicate that the dismissal is confirmed
                        } else {
                            false // Don't allow dismissal for other values
                        }
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.error)
                                .padding(horizontal = 13.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "remove",
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }) {
                    ListItem(
                        headlineContent = {
                            state.downloadAudio?.surah?.arabicName?.let { text -> Text(text) }
                        },
                        supportingContent = {
                            state.downloadAudio?.recitation?.reciter?.arabicName?.let { text ->
                                Text(
                                    text
                                )
                            }
                        },
                        trailingContent = {
                            if (state.state == Download.STATE_COMPLETED) {
                                Icon(Icons.Outlined.Check, contentDescription = "done")
                            } else if (state.state == Download.STATE_DOWNLOADING) {
                                if (showText) {
                                    TextButton(onClick = {
                                        showText = !showText
                                    }) {
                                        Text(
                                            "${((state.progress) * 100).roundToInt()}%",
                                            fontSize = 18.sp,
                                            style = MaterialTheme.typography.titleMediumEmphasized,
                                            color = MaterialTheme.colorScheme.secondary,
                                        )
                                    }
                                } else {
                                    CircularWavyProgressIndicator(
                                        progress = { state.progress },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {
                                                showText = !showText
                                            }

                                    )
                                }

                            } else {
                                IconButton(onClick = { onResume() }) {
                                    Icon(
                                        Icons.Outlined.PauseCircle,
                                        contentDescription = "resume"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.clickable {
                            if (state.state == Download.STATE_DOWNLOADING) {
                                showText = !showText
                            } else {
                                onResume()
                            }
                        }
                    )
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }

}

@PreviewFontScale
@Composable
private fun DownloadingScreenPreview() {
    MostaqemTheme {
        DownloadingScreen(
            downloadedCount = 2,
            downloads = emptyList(),
            onPause = {},
            completedCount = 1,
            onCancel = {},
            isDownloading = false,
            isPaused = false,
            onResume = {}
        )
    }
}