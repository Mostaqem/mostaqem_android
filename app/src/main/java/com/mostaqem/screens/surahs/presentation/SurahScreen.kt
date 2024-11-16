package com.mostaqem.screens.surahs.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.screens.home.data.Suggestion
import com.mostaqem.screens.surahs.data.Surah
import com.mostaqem.screens.player.presentation.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahsScreen(
    modifier: Modifier = Modifier, viewModel: SurahsViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val surahs = viewModel.surahState.collectAsLazyPagingItems()

    val searchSuggestions: List<Suggestion> =
        listOf(
            Suggestion(name = "السورة", example = "السورة: البقرة, الفاتحة"),
            Suggestion(name = "الاية", example = "الاية: ألم, حم"),
            Suggestion(name = "الشيخ", example = "الشيخ: عبدالباسط, المنشاوي"),
        )
    Column(
        modifier = Modifier
            .statusBarsPadding()
    ) {
        SearchBar(inputField = {
            SearchBarDefaults.InputField(query = query,
                onQueryChange = { query = it },
                onSearch = { expanded = false },
                placeholder = { Text("ابحث عن السور") },
                expanded = expanded,
                onExpandedChange = { expanded = it },

                leadingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "delete",
                            )
                        }
                    } else {
                        Icon(Icons.Outlined.Menu, contentDescription = "menu")
                    }
                },
                trailingIcon = {
                    if (expanded) {
                        IconButton(onClick = { expanded = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = "back"
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
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .semantics { traversalIndex = 0f }
                .align(Alignment.CenterHorizontally)

        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                repeat(searchSuggestions.size) { index ->
                    ListItem(headlineContent = { Text(searchSuggestions[index].name) },
                        supportingContent = { Text(searchSuggestions[index].example) },
                        modifier = Modifier.clickable {
                            query = "${searchSuggestions[index].name}: "
                            expanded = false
                        }
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(surahs.itemCount) { index ->
                if (surahs[index] != null) {
                    ListItem(headlineContent = { Text(text = surahs[index]!!.arabicName) },
                        leadingContent = {
                            val currentPlayedSurah: Surah? =
                                playerViewModel.playerState.value.surah
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = surahs[index]?.image,
                                    contentDescription = "surah",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(55.dp)
                                        .clip(RoundedCornerShape(12.dp))

                                )
                                if (currentPlayedSurah != null && currentPlayedSurah == surahs[index]) {
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
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_more_vert_24),
                                    contentDescription = "options"
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            playerViewModel.playerState.value =
                                playerViewModel.playerState.value.copy(surah = surahs[index])
                            playerViewModel.fetchMediaUrl()
                            viewModel.onSurahEvents(SurahEvents.AddSurah(surahs[index]!!))
                        }
                    )

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
                    var errorMessage : String = error.message?: "السيرفر معفن"
                    if (error.message?.contains("Unable to resolve host") == true){
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
                                Icon(Icons.Outlined.Refresh, contentDescription = "refresh")
                            }
                        }
                    }
                }


            }


        }


    }
}


