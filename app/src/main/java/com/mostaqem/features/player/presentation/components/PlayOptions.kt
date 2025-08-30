package com.mostaqem.features.player.presentation.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.DownloadDestination
import com.mostaqem.core.navigation.models.ReadingDestination
import com.mostaqem.core.navigation.models.ReciterDestination
import com.mostaqem.features.player.data.BottomSheetType
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.player.data.toAudioData
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.player.presentation.components.sleep.SleepDialog
import com.mostaqem.features.player.presentation.components.sleep.SleepViewModel
import com.mostaqem.features.player.presentation.components.sleep.toMinSec

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlayOptions(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    playerSurah: PlayerSurah,
    viewModel: SleepViewModel,

    navController: NavController
) {
    var isDownloading by remember { mutableStateOf(false) }
    val showSleepDialog = remember { mutableStateOf(false) }
    val remainingTime by viewModel.remainingTime.collectAsState()
    val context = LocalContext.current
    var repeatMode by remember { mutableIntStateOf(Player.REPEAT_MODE_OFF) }
    var isShuffle by remember { mutableStateOf(false) }
    val surahID = playerSurah.surah?.id
    val recitationID = playerSurah.recitationID
    val isDownloaded = playerViewModel.isDownloaded(surahID, recitationID)

    if (showSleepDialog.value) {
        SleepDialog(
            showSleepDialog = showSleepDialog,
            playerViewModel = playerViewModel,
            viewModel = viewModel,
            remainingTime = remainingTime,
        )
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

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
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {
                val nextRepeatMode = when (repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                    else -> Player.REPEAT_MODE_OFF
                }
                repeatMode = nextRepeatMode
                playerViewModel.repeat(nextRepeatMode)
            }) {
                Icon(
                    imageVector = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        Player.REPEAT_MODE_ALL -> Icons.Default.Repeat
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "repeat",
                    tint = if (repeatMode == Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    ) else MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

            IconButton(onClick = {
                isShuffle = !isShuffle
                playerViewModel.shuffle(isShuffle)
            }) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "shuffle",
                    tint = if (!isShuffle) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

            val isFavorited by playerViewModel.isFavorited.collectAsState()

            IconButton(
                onClick = {
                    playerViewModel.favorite(playerSurah.toAudioData())
                },
                shape = IconButtonDefaults.largeRoundShape,


            ) {
                Icon(
                    if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "favorite",
                    tint = if (!isFavorited) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(15.dp))
            Box {
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_more_horiz_24),
                        contentDescription = "menu",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (surahID != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.read_chapter)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_menu_book_24),
                                    contentDescription = "read"
                                )
                            },
                            onClick = {
                                val surahName = playerSurah.surah.arabicName
                                expanded = false

                                navController.navigate(
                                    ReadingDestination(
                                        surahID = surahID,
                                        surahName = surahName.toString()
                                    )
                                )

                            }
                        )
                    }
                    if (surahID != null){
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.share)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "share"
                                )
                            },
                            onClick = {
                                val recitationID = playerSurah.recitationID
                                val deepLinkUrl =
                                    "https://mostaqemapp.online/quran/${surahID}/${recitationID}"
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, deepLinkUrl)
                                }
                                expanded = false
                                context.startActivity(Intent.createChooser(shareIntent, "Share via"))

                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.change_reciter)) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person, contentDescription = "reciter",
                            )
                        },
                        onClick = {
                            navController.navigate(ReciterDestination)
                            expanded = false

                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.change_recitation)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_radio_button_checked_24),
                                contentDescription = "recitation",
                            )
                        },
                        onClick = {
                            playerViewModel.showBottomSheet(BottomSheetType.Recitations)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Timer") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Bedtime,
                                contentDescription = "sleep_timer",

                                )
                        },
                        onClick = {
                            showSleepDialog.value = true
                            expanded = false
                        }
                    )
                    if (!isDownloaded) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.download)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.download),
                                    contentDescription = "download"
                                )
                            },
                            onClick = {
                                if (!isDownloading) playerViewModel.download(
                                    audio = playerSurah.toAudioData(),
                                    context
                                )
                                navController.navigate(DownloadDestination)
                                isDownloading = true
                                expanded = false
                            }
                        )

                    }


                }
            }
        }
    }
}

