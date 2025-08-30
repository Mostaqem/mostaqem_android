package com.mostaqem.features.reciters.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mostaqem.core.navigation.models.SearchDestination
import com.mostaqem.dataStore
import com.mostaqem.features.personalization.presentation.PersonalizationViewModel
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.data.ReciterOption
import com.mostaqem.features.reciters.domain.ReciterEvents
import com.mostaqem.features.reciters.presentation.recitations.RecitationList
import com.mostaqem.features.settings.data.AppSettings


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReciterScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    personViewModel: PersonalizationViewModel,
    viewModel: ReciterViewModel,
) {
    val reciters = viewModel.reciterState.collectAsLazyPagingItems()
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code
    val isArabic = languageCode == "ar"
    val currentPlayer = playerViewModel.playerState.value

    val defaultReciter by playerViewModel.defaultReciterState.collectAsState()
    val defaultRecitation by playerViewModel.defaultRecitation.collectAsState()

    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showRecitations by remember { mutableStateOf(false) }
    var isDefaultOptionClicked by viewModel.showDefaultOption

    if (showRecitations) {
        ModalBottomSheet(onDismissRequest = { showRecitations = false }) {
            RecitationList(
                viewModel = viewModel,
                playerViewModel = playerViewModel,
                selectedReciter = if (isDefaultOptionClicked) defaultReciter else currentPlayer.reciter
            ) { recitation ->
                if (isDefaultOptionClicked) {
                    personViewModel.changeRecitationID(recitation)
                    playerViewModel.changeRecitation(recitation.id)
                    isDefaultOptionClicked = false
                } else {
                    playerViewModel.changeRecitation(recitation.id)
                }
            }
        }
    }
    BackHandler(isDefaultOptionClicked) {
        isDefaultOptionClicked = false
    }
    Box(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .safeContentPadding()

        ) {
            if (!isDefaultOptionClicked) {
                item {

                    Column {
                        IconButton(
                            onClick = { navController.popBackStack() },
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back "
                            )
                        }
                        Text(
                            "Now Playing",
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            modifier = Modifier.padding(18.dp)
                        )
                        ListItem(
                            headlineContent = {
                                Column() {
                                    Text(
                                        currentPlayer.surah!!.arabicName,
                                        style = MaterialTheme.typography.titleMediumEmphasized
                                    )
                                    Text(
                                        currentPlayer.reciter.arabicName,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.titleSmallEmphasized
                                    )

                                }
                            },
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(MaterialShapes.Square.toShape())
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .size(50.dp)
                                ) {
                                    LoadingIndicator(
                                        modifier = Modifier.size(35.dp),
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        polygons = listOf(
                                            MaterialShapes.Flower,
                                            MaterialShapes.Circle
                                        )
                                    )
                                }
                            }
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Default Reciter",
                                    style = MaterialTheme.typography.titleLargeEmphasized,
                                )
                            },
                            supportingContent = {
                                Text(
                                    "The reciter that you will hear mostly, click on edit to change",
                                    style = MaterialTheme.typography.titleSmallEmphasized,
                                )
                            }
                        )
                        ListItem(
                            trailingContent = {
                                IconButton(onClick = {
                                    isDefaultOptionClicked = true
                                }) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "change default")
                                }
                            },
                            headlineContent = {
                                Column() {
                                    Text(
                                        if (isArabic) defaultReciter.arabicName else defaultReciter.englishName,
                                        style = MaterialTheme.typography.titleMediumEmphasized
                                    )
                                    Text(
                                        if (isArabic) defaultRecitation.name else defaultRecitation.englishName
                                            ?: "Hafs An Assem",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.titleSmallEmphasized
                                    )

                                }
                            },
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clip(MaterialShapes.Square.toShape())
                                        .background(MaterialTheme.colorScheme.tertiary)
                                        .size(50.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(MaterialShapes.Arch.toShape())
                                            .background(MaterialTheme.colorScheme.onTertiary)
                                            .size(25.dp),

                                        )
                                }
                            }
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Reciters",
                                    style = MaterialTheme.typography.titleLargeEmphasized,
                                )
                            },
                            supportingContent = {
                                Text(
                                    "You can click to change current playing reciter",
                                    style = MaterialTheme.typography.titleSmallEmphasized,

                                    )
                            }
                        )


                    }
                }
            } else {
                item {
                    ListItem(
                        modifier = Modifier.padding(18.dp),
                        headlineContent = {
                            Text(
                                "Press long on the reciter you want to make it default",
                                style = MaterialTheme.typography.titleLargeEmphasized,
                            )
                        },
                        supportingContent = {
                            Text(
                                "You can always change that later",
                                style = MaterialTheme.typography.titleSmallEmphasized,
                            )
                        }
                    )
                }
            }

            val reciterLoadState = reciters.loadState.refresh

            when (reciterLoadState) {
                is LoadState.Error -> {
                    item { Text("Error") }
                }

                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) { LoadingIndicator() }
                    }
                }

                is LoadState.NotLoading -> {
                    items(reciters.itemCount) { index ->
                        val reciter = reciters[index]
                        val currentPlayingReciter: Boolean =
                            currentPlayer.reciter.id == reciters[index]?.id
                        val animatedColor by animateColorAsState(
                            if (currentPlayingReciter) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                        )
                        ListItem(
                            trailingContent = {
                                Icon(Icons.Outlined.PlayArrow, "play")
                            },
                            headlineContent = {
                                if (isArabic) reciter?.arabicName else reciter?.englishName?.let {
                                    Text(
                                        it ,
                                        style = MaterialTheme.typography.titleMediumEmphasized
                                    )
                                }
                            },
                            leadingContent = {
                                if (currentPlayingReciter) {
                                    LoadingIndicator(

                                        modifier = Modifier.size(50.dp),
                                        polygons = listOf(
                                            MaterialShapes.Square,
                                            MaterialShapes.Clover4Leaf
                                        )
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(MaterialShapes.Cookie6Sided.toShape())
                                            .background(animatedColor)
                                            .size(50.dp)
                                    )
                                }

                            },
                            colors = ListItemDefaults.colors(containerColor = if (currentPlayingReciter) MaterialTheme.colorScheme.primaryContainer else Color.Transparent),
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        if (isDefaultOptionClicked) {
                                            personViewModel.saveDefaultReciter(reciter!!)
                                            playerViewModel.changeDefaultReciter()
                                            if (playerViewModel.playerState.value.surah != null) {
                                                playerViewModel.changeReciter(reciter)
                                            }
                                            isDefaultOptionClicked = false
                                            fabMenuExpanded = false
                                        } else {
                                            playerViewModel.changeReciter(reciter!!)
                                            viewModel.onReciterEvents(
                                                ReciterEvents.AddReciter(
                                                    reciter
                                                )
                                            )
                                            fabMenuExpanded = false

                                        }
                                    }, onLongPress = {
                                        personViewModel.saveDefaultReciter(reciter!!)
                                        playerViewModel.changeDefaultReciter()
                                        if (playerViewModel.playerState.value.surah != null) {
                                            playerViewModel.changeReciter(reciter)
                                        }
                                        isDefaultOptionClicked = false
                                        fabMenuExpanded = false
                                    })
                                }


                        )
                    }

                }
            }
        }

        val items =
            listOf(
                ReciterOption(
                    icon = Icons.Outlined.Book,
                    name = if (isDefaultOptionClicked) "Change Default Recitation" else "Change Recitation",
                    onClick = { showRecitations = true }),
                ReciterOption(
                    icon = Icons.Outlined.Search,
                    name = "Search",
                    onClick = { navController.navigate(SearchDestination) }),

                )

        FloatingActionButtonMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            expanded = fabMenuExpanded,
            button = {
                ToggleFloatingActionButton(
                    modifier =
                        Modifier
                            .semantics {
                                traversalIndex = -1f
                                stateDescription =
                                    if (fabMenuExpanded) "Expanded" else "Collapsed"
                                contentDescription = "Toggle menu"
                            }
                            .animateFloatingActionButton(
                                visible = true,
                                alignment = Alignment.BottomEnd,
                            ),
                    checked = fabMenuExpanded,
                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Edit
                        }
                    }
                    Icon(
                        painter = rememberVectorPainter(imageVector),
                        contentDescription = null,
                        modifier = Modifier.animateIcon({ checkedProgress }),
                    )
                }
            },
        ) {
            items.forEachIndexed { i, item ->
                FloatingActionButtonMenuItem(
                    modifier =
                        Modifier.semantics {
                            isTraversalGroup = true
                            if (i == items.size - 1) {
                                customActions =
                                    listOf(
                                        CustomAccessibilityAction(
                                            label = "Close menu",
                                            action = {
                                                fabMenuExpanded = false
                                                true
                                            },
                                        )
                                    )
                            }
                        },
                    onClick = {
                        fabMenuExpanded = false
                        item.onClick()
                    },
                    icon = { Icon(item.icon, contentDescription = null) },
                    text = { Text(text = item.name) },
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun ReciterScreenPreview() {


}