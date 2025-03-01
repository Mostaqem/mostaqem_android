package com.mostaqem.features.surahs.presentation

import android.util.Log
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.presentation.components.SurahOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahsScreen(
    viewModel: SurahsViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel,
    navController: NavController
) {
    var query by rememberSaveable { mutableStateOf("") }
    val querySurahsList = remember { viewModel.queryState }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val queryLoading = viewModel.loading.value
    val surahs = viewModel.surahState.collectAsLazyPagingItems()
    var isOptionsShown by remember {
        mutableStateOf(false)
    }
    var selectedSurah: Surah? by remember {
        mutableStateOf(null)
    }
    var selectedReciter: Reciter? by remember { mutableStateOf(null) }
    var selectedRecitation: Int? by remember { mutableStateOf(null) }

    val bottomSheetState = rememberModalBottomSheetState()
    val downloadedSurahs by viewModel.downloaded.collectAsState()
    var showDownloads by remember { mutableStateOf(false) }
    val player = playerViewModel.playerState.value


    Column {
        SearchBar(inputField = {
            SearchBarDefaults.InputField(query = query,
                onQueryChange = {
                    query = it
                    viewModel.searchSurahs(it)
                },
                onSearch = {
                    expanded = false
                    querySurahsList.value = emptyList()
                },
                placeholder = { Text("ابحث عن السورة") },
                expanded = expanded,
                onExpandedChange = { expanded = it },

                leadingIcon = {
                    if (expanded) {
                        IconButton(onClick = {
                            query = ""
                            viewModel.searchSurahs(null)
                            expanded = false

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
                            imageVector = Icons.Outlined.Search, contentDescription = "search"
                        )
                    }


                })
        },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .semantics { traversalIndex = 0f }
                .align(Alignment.CenterHorizontally)
                .then(
                    if (!expanded) {
                        Modifier.padding(horizontal = 8.dp)
                    } else {
                        Modifier
                    }
                )

        ) {
            if (queryLoading && querySurahsList.value.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()

                }
            }
            if (querySurahsList.value.isEmpty() && query.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "لا يوجد سورة بهذا الاسم")

                }
            }
            if (query.isNotEmpty() && !queryLoading && querySurahsList.value.isNotEmpty()) {
                val surahs = querySurahsList.value
                val surahsCount: String = surahs.size.toArabicNumbers()
                val surahArabic: String = if (surahs.size > 1) "سور" else "سورة"
                Text(
                    text = "$surahsCount $surahArabic ",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    items(querySurahsList.value.size) { index ->
                        val currentPlayedSurah: Surah? = player.surah
                        val isCurrentSurahPlayed: Boolean =
                            currentPlayedSurah != null && currentPlayedSurah.arabicName == surahs[index].arabicName
                        ListItem(headlineContent = { Text(text = surahs[index].arabicName) },
                            supportingContent = { Text(text = surahs[index].complexName) },
                            leadingContent = {
                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        model = surahs[index].image,
                                        contentDescription = "surah",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(55.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    if (isCurrentSurahPlayed) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    Color.Black.copy(alpha = 0.8f)
                                                )
                                                .size(55.5.dp)
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.outline_graphic_eq_24),
                                            contentDescription = "Playing",
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }

                            },
                            trailingContent = {
                                IconButton(onClick = {
                                    selectedSurah = surahs[index]
                                    isOptionsShown = true

                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                                        contentDescription = "play"
                                    )
                                }

                            },
                            colors = if (isCurrentSurahPlayed) ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,

                                ) else ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable {
                                playerViewModel.changeSurah(surahs[index])
                                playerViewModel.fetchMediaUrl()
                                viewModel.onSurahEvents(SurahEvents.AddSurah(surahs[index]))
                            })
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(if (showDownloads) "السور المتحملة" else "السور")
            IconButton(onClick = {
                showDownloads = !showDownloads
                if (showDownloads) viewModel.displayDownloaded()

            }) {
                Icon(painter = painterResource(R.drawable.filter), contentDescription = "filter")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isOptionsShown) {
            ModalBottomSheet(
                onDismissRequest = { isOptionsShown = false },
                sheetState = bottomSheetState
            ) {
                SurahOptions(
                    selectedSurah = selectedSurah,
                    playerViewModel = playerViewModel,
                    navController = navController,
                    selectedReciter = selectedReciter,
                    selectedRecitationID = selectedRecitation

                ) {
                    isOptionsShown = false
                }
            }
        }
        Crossfade(targetState = showDownloads) { value ->

            when (value) {
                true -> {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        items(downloadedSurahs) {
                            ListItem(
                                headlineContent = {
                                    Text(it.surah.arabicName)
                                },
                                supportingContent = {
                                    Text(it.recitation.reciter.arabicName)
                                },
                                leadingContent = {
                                    AsyncImage(
                                        model = it.surah.image,
                                        contentDescription = "surah",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(55.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = {
                                        selectedSurah = it.surah
                                        selectedReciter = it.recitation.reciter
                                        selectedRecitation = it.recitation.id
                                        isOptionsShown = true
                                    }) {
                                        Icon(
                                            Icons.Outlined.MoreVert,
                                            contentDescription = "options"
                                        )
                                    }
                                },
                                modifier = Modifier.clickable { playerViewModel.localPlay(it) }
                            )
                        }
                    }

                }

                false -> {
                    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                        items(surahs.itemCount) { index ->
                            val currentPlayedSurah: Surah? = player.surah
                            val isCurrentSurahPlayed: Boolean =
                                currentPlayedSurah != null && currentPlayedSurah.arabicName == surahs[index]?.arabicName
                            if (surahs[index] != null) {
                                ListItem(headlineContent = { Text(text = surahs[index]!!.arabicName) },
                                    supportingContent = { Text(text = surahs[index]!!.complexName) },
                                    leadingContent = {
                                        Box(contentAlignment = Alignment.Center) {
                                            AsyncImage(
                                                model = surahs[index]?.image,
                                                contentDescription = "surah",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(55.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                            )
                                            if (isCurrentSurahPlayed) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                            Color.Black.copy(alpha = 0.8f)
                                                        )
                                                        .size(55.5.dp)
                                                )
                                                Icon(
                                                    painter = painterResource(id = R.drawable.outline_graphic_eq_24),
                                                    contentDescription = "Playing",
                                                    tint = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                        }

                                    },
                                    trailingContent = {
                                        IconButton(onClick = {
                                            selectedSurah = surahs[index]
                                            isOptionsShown = true

                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_more_vert_24),
                                                contentDescription = "play"
                                            )
                                        }

                                    },
                                    colors = if (isCurrentSurahPlayed) ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,

                                        ) else ListItemDefaults.colors(),
                                    modifier = Modifier.clickable {
                                        playerViewModel.changeSurah(surahs[index]!!)
                                        playerViewModel.fetchMediaUrl()
                                        viewModel.onSurahEvents(SurahEvents.AddSurah(surahs[index]!!))
                                    })
                            }
                        }
                        val surahLoadState = surahs.loadState.refresh
                        when {
                            surahLoadState is LoadState.Loading -> {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .padding(24.dp)
                                            .fillParentMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            surahLoadState is LoadState.Error -> {
                                val error = surahLoadState.error
                                var errorMessage = "السيرفر معفن"
                                if (error.message!!.contains("connect")) {
                                    errorMessage = "لا يوجد انترنت"
                                }
                                item {
                                    Column(
                                        modifier = Modifier.fillParentMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = errorMessage,
                                            textAlign = TextAlign.Center,
                                        )
                                        IconButton(onClick = { surahs.retry() }) {
                                            Icon(
                                                Icons.Outlined.Refresh,
                                                contentDescription = "refresh"
                                            )
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
            }
        }


    }

}


