package com.mostaqem.features.player.presentation.components

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.ReadingDestination
import com.mostaqem.features.player.data.BottomSheetType
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.player.presentation.components.sleep.SleepDialog
import com.mostaqem.features.player.presentation.components.sleep.SleepViewModel
import com.mostaqem.features.player.presentation.components.sleep.toMinSec

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
                )
            }
            Spacer(modifier = Modifier.width(15.dp))

            if (remainingTime == 0L) {
                IconButton(onClick = {
                    showSleepDialog.value = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.bedtime),
                        contentDescription = "sleep_timer",

                        )
                }
            } else {
                TextButton(onClick = {
                    showSleepDialog.value = true
                }) {
                    Text(remainingTime.toMinSec())
                }
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
            IconButton(onClick = {
                playerViewModel.showBottomSheet(BottomSheetType.Reciters)
            }) {
                Icon(
                    Icons.Outlined.Person, contentDescription = "reciter",

                    )
            }

            Spacer(modifier = Modifier.width(15.dp))
            Box {
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_more_horiz_24),
                        contentDescription = "menu"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(

                        text = { Text(stringResource(R.string.read_chapter)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_menu_book_24),
                                contentDescription = "read"
                            )
                        },
                        onClick = {
                            val surahID = playerSurah.surah?.id ?: 0
                            val surahName = playerSurah.surah?.arabicName
                            navController.navigate(
                                ReadingDestination(
                                    surahID = surahID,
                                    surahName = surahName.toString()
                                )
                            )
                        }
                    )
                    DropdownMenuItem(

                        text = { Text(stringResource(R.string.share)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "share"
                            )
                        },
                        onClick = {
                            val surahID = playerSurah.surah?.id
                            val recitationID = playerSurah.recitationID
                            val deepLinkUrl =
                                "https://mostaqemapp.online/quran/${surahID}/${recitationID}"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, deepLinkUrl)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))

                        }
                    )
                    if (!playerViewModel.isCurrentSurahDownloaded()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.download)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.download),
                                    contentDescription = "download"
                                )
                            },
                            onClick = {
                                if (!isDownloading) playerViewModel.download()
                                isDownloading = true
                            }
                        )
                    }

                }
            }
        }
    }
}

