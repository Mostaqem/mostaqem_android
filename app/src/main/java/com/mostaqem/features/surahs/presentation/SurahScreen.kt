package com.mostaqem.features.surahs.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.presentation.components.SurahListItem
import com.mostaqem.features.surahs.presentation.components.SurahOptions
import com.mostaqem.features.surahs.presentation.components.SurahSortBottomSheet

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SurahsScreen(
    viewModel: SurahsViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel,
    navController: NavController,
    onHideBottom: (Boolean) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val querySurahsList = remember { viewModel.queryState }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val queryLoading by viewModel.loading.collectAsState()
    val surahs = viewModel.surahState.collectAsLazyPagingItems()
    var isOptionsShown by remember {
        mutableStateOf(false)
    }

    var selectedSurahName: String? by remember { mutableStateOf(null) }
    var selectedSurahID: Int by remember { mutableIntStateOf(0) }

    val bottomSheetState = rememberModalBottomSheetState()
    val downloadedSurahs by viewModel.downloadedAudios.collectAsState()
    var showDownloads by remember { mutableStateOf(false) }
    val player = playerViewModel.playerState.value
    val context = LocalContext.current

    val isArabic = MaterialTheme.typography.titleLarge.fontFamily == kufamFontFamily

    val isBottomSheetShown = remember { mutableStateOf(false) }

    val defaultSortBy = context.dataStore.data.collectAsState(AppSettings()).value.sortBy
    val defaultReciter by playerViewModel.defaultReciterState.collectAsState()
    val defaultReciterName = if (isArabic) defaultReciter.arabicName else defaultReciter.englishName
    if (isBottomSheetShown.value && !showDownloads) {
        SurahSortBottomSheet(
            viewModel = viewModel,
            defaultSortBy = defaultSortBy
        ) {
            isBottomSheetShown.value = false
        }
    }
    Box(contentAlignment = Alignment.TopCenter) {
        LazyColumn(modifier = Modifier.padding(top = 100.dp)) {


            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        isBottomSheetShown.value = true

                    }) {
                        Text(
                            if (showDownloads) stringResource(R.string.downloads) else stringResource(
                                R.string.surahs
                            )
                        )
                    }
                    TextButton(onClick = {
                        showDownloads = !showDownloads
                    }) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.downloads))
                            Icon(Icons.Outlined.ArrowDropDown, contentDescription = "")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                if (isOptionsShown) {
                    ModalBottomSheet(
                        onDismissRequest = { isOptionsShown = false }, sheetState = bottomSheetState
                    ) {
                        SurahOptions(
                            selectedSurah = selectedSurahName,
                            playerViewModel = playerViewModel,
                            navController = navController,

                            selectedSurahID = selectedSurahID,
                            context = context
                        ) {
                            isOptionsShown = false
                        }
                    }
                }
            }

            if (showDownloads) {
                items(downloadedSurahs) {
                    ListItem(headlineContent = {
                        Text(it.title)
                    }, supportingContent = {
                        Text(it.reciter)
                    }, leadingContent = {
                        AsyncImage(
                            model = "https://img.freepik.com/premium-photo/illustration-mosque-with-crescent-moon-stars-simple-shapes-minimalist-flat-design_217051-15556.jpg",
                            contentDescription = "surah",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(12.dp))


                        )
                    }, trailingContent = {
                        IconButton(onClick = {
                            selectedSurahName = it.title
                            selectedSurahID = it.surahID.toInt()

                            isOptionsShown = true
                        }) {
                            Icon(
                                Icons.Outlined.MoreVert, contentDescription = "options"
                            )
                        }
                    }, modifier = Modifier.clickable {
                        playerViewModel.fetchMediaUrl(
                            surahId = it.surahID.toInt(),
                            recID = it.recitationID.toInt()
                        )
                    })
                }
            } else {
                val surahLoadState = surahs.loadState.refresh
                when (surahLoadState) {
                    is LoadState.Loading -> {
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillParentMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LoadingIndicator()
                            }
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                IconButton(onClick = { surahs.retry() }) {
                                    Icon(
                                        Icons.Outlined.Refresh, contentDescription = "refresh"
                                    )
                                }
                            }
                        }
                    }

                    is LoadState.NotLoading -> {
                        items(surahs.itemCount) { index ->
                            val currentPlayedSurah: Surah? = player.surah
                            val isCurrentSurahPlayed: Boolean =
                                currentPlayedSurah != null && (currentPlayedSurah.arabicName == surahs[index]?.complexName || currentPlayedSurah.arabicName == surahs[index]?.arabicName)

                            if (surahs[index] != null) {
                                SurahListItem(
                                    surah = surahs[index]!!,
                                    isCurrentSurahPlayed = isCurrentSurahPlayed,
                                    isArabic = isArabic,
                                    defaultReciterName = defaultReciterName,
                                    onMenu = {
                                        selectedSurahName =
                                            if (isArabic) surahs[index]?.arabicName else surahs[index]?.complexName
                                        selectedSurahID = surahs[index]?.id ?: 0
                                        isOptionsShown = true
                                    }) {
                                    playerViewModel.changeSurah(surahs[index]!!)
                                    playerViewModel.fetchMediaUrl()
                                    viewModel.onSurahEvents(SurahEvents.AddSurah(surahs[index]!!))
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        SearchBar(
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            5.dp
                        ),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                    ),
                    onQueryChange = {
                        query = it
                        viewModel.searchSurahs(it)
                    },
                    onSearch = {
                        expanded = false
                        querySurahsList.value = emptyList()
                    },
                    placeholder = { Text(stringResource(R.string.search_chapter)) },
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = it
                        onHideBottom(it)
                    },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(onClick = {
                                query = ""
                                viewModel.searchSurahs(null)
                                expanded = false
                                onHideBottom(false)


                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "back"
                                )
                            }
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                query = ""
                                viewModel.searchSurahs(null)

                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "delete",
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "search"
                            )
                        }
                    })
            },
            expanded = expanded,
            onExpandedChange = {
                expanded = it
                onHideBottom(it)
            },
            modifier = Modifier
                .semantics { traversalIndex = 0f }
                .then(
                    if (!expanded) {
                        Modifier.padding(horizontal = 15.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            when {
                queryLoading && querySurahsList.value.isNotEmpty() -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadingIndicator()
                    }
                }

                querySurahsList.value.isEmpty() && query.isNotEmpty() -> {
                    Box(
                        contentAlignment = Alignment.Center,

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = stringResource(R.string.no_chapter))
                    }
                }

                query.isNotEmpty() && !queryLoading && querySurahsList.value.isNotEmpty() -> {
                    val surahs = querySurahsList.value
                    val surahsCount: String =
                        if (isArabic) surahs.size.toArabicNumbers() else surahs.size.toString()
                    val surahArabic: String =
                        if (surahs.size > 1) stringResource(R.string.surahs) else stringResource(
                            R.string.surah
                        )
                    LazyColumn(
                    ) {
                        item {
                            Text(
                                text = "$surahsCount $surahArabic",
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.W600,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(
                            items = surahs,
                        ) { surah ->
                            val currentPlayedSurah: Surah? = player.surah
                            val isCurrentSurahPlayed =
                                currentPlayedSurah?.arabicName == surah.arabicName

                            SurahListItem(
                                surah = surah,
                                isCurrentSurahPlayed = isCurrentSurahPlayed,
                                isArabic = isArabic,
                                defaultReciterName = defaultReciterName,
                                onMenu = {
                                    selectedSurahName =
                                        if (isArabic) surah.arabicName else surah.complexName
                                    selectedSurahID = surah.id
                                    isOptionsShown = true
                                }
                            ) {
                                query = ""
                                expanded = false
                                onHideBottom(false)
                                playerViewModel.changeSurah(surah)
                                playerViewModel.fetchMediaUrl()
                                viewModel.onSurahEvents(SurahEvents.AddSurah(surah))
                            }
                        }
                    }
                }
            }
        }
    }
}