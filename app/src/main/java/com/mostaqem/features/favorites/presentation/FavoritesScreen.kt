package com.mostaqem.features.favorites.presentation

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.features.personalization.presentation.components.LargeTopBar
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.surahs.data.SelectedSurahState
import com.mostaqem.features.surahs.presentation.components.SurahOptions

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel
) {
    val favorites by viewModel.favorities.collectAsState()
    val isPlaying = playerViewModel.playerState.value.surah != null
    val animatedPadding by animateIntAsState(
        targetValue = if (isPlaying) 100 else 0,
        label = "animate_float_padding",
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val context = LocalContext.current
    var openOptionsSheet by remember { mutableStateOf(false) }
    var selectedState: SelectedSurahState? by remember { mutableStateOf(null) }
    var selectedReciterName: String? by remember { mutableStateOf(null) }
    var selectedReciterID: Int? by remember { mutableStateOf(null) }
    var selectedRecitationID: Int? by remember { mutableStateOf(null) }

    Scaffold(topBar = {
        LargeTopBar(navController, title = stringResource(R.string.favorites))
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                playerViewModel.playPlaylist(favorites)
            },
            modifier = Modifier.padding(bottom = animatedPadding.coerceAtLeast(0).dp)
        ) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = "playPlaylist")
        }
    }) {
        if (favorites.isEmpty()) {

        } else {
            if (openOptionsSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        openOptionsSheet = false
                    }
                ) {
                    SurahOptions(
                        playerViewModel = playerViewModel,
                        navController = navController,
                        selectedSurah = selectedState!!.surahName,
                        selectedSurahID = selectedState!!.surahID,
                        selectedReciter = selectedReciterName,
                        selectedReciterID = selectedReciterID,
                        selectedRecitation = selectedRecitationID,
                        context = context
                    ) {
                        openOptionsSheet = false
                    }
                }
            }
            LazyColumn(contentPadding = it) {
                items(favorites, key = { f -> f.id }) { favorite ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                viewModel.removeFavorite(favorite)
                                true
                            } else {
                                false
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
                            leadingContent = {
                                AsyncImage(
                                    model = "https://img.freepik.com/premium-photo/illustration-mosque-with-crescent-moon-stars-simple-shapes-minimalist-flat-design_217051-15556.jpg",
                                    contentDescription = "surah",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(18))
                                )
                            },
                            headlineContent = {
                                Text(favorite.surahName.toString())
                            },
                            supportingContent = {
                                Text(favorite.reciter)
                            },
                            trailingContent = {

                                IconButton(onClick = {
                                    selectedState = SelectedSurahState(
                                        surahName = favorite.surahName!!,
                                        surahID = favorite.surahID.toInt(),
                                    )
                                    selectedReciterName = favorite.reciter
                                    selectedReciterID = favorite.reciterID.toInt()
                                    selectedRecitationID = favorite.recitationID.toInt()
                                    openOptionsSheet = true
                                }) {
                                    Icon(
                                        Icons.Outlined.MoreVert, contentDescription = "options"
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                playerViewModel.fetchMediaUrl(
                                    recID = favorite.recitationID.toInt(),
                                    surahId = favorite.surahID.toInt()
                                )
                            }

                        )
                    }


                }

            }
        }

    }

}