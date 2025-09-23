package com.mostaqem.features.search.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.PlayerDestination
import com.mostaqem.dataStore
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.personalization.presentation.PersonalizationViewModel
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.domain.ReciterEvents
import com.mostaqem.features.reciters.presentation.ReciterViewModel
import com.mostaqem.features.settings.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    personViewModel: PersonalizationViewModel,
    reciterViewModel: ReciterViewModel
) {
    val queryReciters = remember { reciterViewModel.queryReciters }
    var query by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(true) }
    var isDefaultOptionClicked by reciterViewModel.showDefaultOption
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code
    val isArabic = languageCode == "ar"


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                modifier = Modifier.focusRequester(focusRequester),
                onQueryChange = {
                    query = it
                    reciterViewModel.searchReciters(it)
                },
                onSearch = {
                    reciterViewModel.searchReciters(it)
                },
                placeholder = { Text(stringResource(R.string.search_reciter)) },
                expanded = true,
                onExpandedChange = {

                },
                leadingIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                trailingIcon = {
                    if (query != "") {
                        IconButton(
                            onClick = {
                                query = ""
                                navController.popBackStack()
                            },
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "remove"
                            )
                        }
                    }

                }
            )
        },
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            if (!expanded) {
                navController.popBackStack()
            }
        },
    ) {
        if (query.isNotEmpty() && queryReciters.value.isNotEmpty()) {
            val reciters = queryReciters.value


            LazyColumn {
                items(reciters) { reciter ->
                    val currentPlayingReciter: Boolean =
                        playerViewModel.playerState.value.reciter.id == reciter.id
                    val animatedColor by animateColorAsState(
                        if (currentPlayingReciter) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                    )
                    ListItem(
                        trailingContent = {
                            Icon(Icons.Outlined.PlayArrow, "play")
                        },
                        headlineContent = {
                            Text(
                                if (isArabic) reciter.arabicName else reciter.englishName,
                                style = MaterialTheme.typography.titleMediumEmphasized
                            )
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
                            .padding(18.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    if (isDefaultOptionClicked) {
                                        personViewModel.saveDefaultReciter(reciter)
                                        playerViewModel.changeDefaultReciter()
                                        if (playerViewModel.playerState.value.surah != null) {
                                            playerViewModel.changeReciter(reciter)
                                        }
                                        navController.popBackStack()
                                        isDefaultOptionClicked = false
                                    } else {
                                        playerViewModel.changeReciter(reciter)
                                        reciterViewModel.onReciterEvents(
                                            ReciterEvents.AddReciter(
                                                reciter
                                            )
                                        )
                                        navController.popBackStack(
                                            route = PlayerDestination,
                                            inclusive = false
                                        )
                                    }

                                }, onLongPress = {
                                    personViewModel.saveDefaultReciter(reciter)
                                    playerViewModel.changeDefaultReciter()
                                    if (playerViewModel.playerState.value.surah != null) {
                                        playerViewModel.changeReciter(reciter)
                                    }
                                    isDefaultOptionClicked = false
                                    navController.popBackStack()

                                })
                            }

                    )
                }
            }
        }

    }
}
