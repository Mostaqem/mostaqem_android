package com.mostaqem.features.personalization.presentation.reciter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.features.language.presentation.LanguageViewModel
import com.mostaqem.features.personalization.presentation.PersonalizationViewModel
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.presentation.ReciterScreen
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterOption(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    viewModel: PersonalizationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val appSettings by context.dataStore.data.collectAsState(initial = AppSettings())
    val savedReciter = appSettings.reciterSaved
    val savedRecitationID = appSettings.recitationID
    val reciterViewModel: ReciterViewModel = hiltViewModel()
    val recitations by reciterViewModel.recitationState.collectAsState()
    var showRecitations by remember { mutableStateOf(false) }
    reciterViewModel.getRecitations(savedReciter.id)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val showReciters = remember { mutableStateOf(false) }
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val isArabic = languageCode == "ar"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    showRecitations = !showRecitations
                })
            }

    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = savedReciter.image,
                        contentDescription = "reciter",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .size(60.dp)

                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        text = if (isArabic) savedReciter.arabicName else savedReciter.englishName,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,


                        )
                }
                Row {
                    IconButton(onClick = {
                        showRecitations = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_radio_button_checked_24),
                            contentDescription = "recitation",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = { showReciters.value = true }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "edit",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (showReciters.value) {
                ModalBottomSheet(sheetState = bottomSheetState, onDismissRequest = {
                    showReciters.value = false
                }) {
                    ReciterScreen(
                        isDefaultBtn = true,
                        playerViewModel = playerViewModel,
                        bottomSheet = showReciters
                    )
                }
            }

            if (showRecitations) {
                ModalBottomSheet(onDismissRequest = { showRecitations = false }) {
                    LazyColumn {
                        item {
                            Text(
                                stringResource(R.string.recitations),
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(recitations) {
                            AnimatedVisibility(visible = showRecitations) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = if (isArabic) it.name else it.englishName
                                                ?: it.name
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        viewModel.changeRecitationID(it.id)
                                        playerViewModel.changeRecitation(it.id)
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                    trailingContent = {
                                        RadioButton(
                                            selected = it.id == savedRecitationID,
                                            onClick = {
                                                viewModel.changeRecitationID(it.id)
                                                playerViewModel.changeRecitation(it.id)

                                            })
                                    })
                            }

                        }
                    }
                }
            }
        }


    }
}
