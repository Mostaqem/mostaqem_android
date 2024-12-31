package com.mostaqem.screens.reciters.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.mostaqem.screens.home.data.Suggestion
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.reciters.data.reciter.Reciter
import com.mostaqem.screens.reciters.domain.ReciterEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterScreen(
    modifier: Modifier = Modifier,
    viewModel: ReciterViewModel,
) {
    val playerViewModel: PlayerViewModel = hiltViewModel()
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val reciters = viewModel.reciterState.collectAsLazyPagingItems()

    val searchSuggestions: List<Suggestion> = listOf(
        Suggestion(name = "السورة", example = "السورة: البقرة, الفاتحة"),
        Suggestion(name = "الاية", example = "الاية: ألم, حم"),
        Suggestion(name = "الشيخ", example = "الشيخ: عبدالباسط, المنشاوي"),
    )
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SearchBar(inputField = {
            SearchBarDefaults.InputField(query = query,
                onQueryChange = { query = it },
                onSearch = { query ->
                    expanded = false
                    viewModel.onSearchReciters(query)
                },
                placeholder = { Text("ابحث عن الشيخ") },
                expanded = expanded,
                onExpandedChange = { expanded = it },

                leadingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            viewModel.onSearchReciters(null)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "delete",
                            )
                        }
                    } else {
                        if (!expanded) Icon(Icons.Outlined.Menu, contentDescription = "menu")
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

        ) {

        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            items(reciters.itemCount) { index ->
                if (reciters[index] != null) {
                    val currentPlayingReciter: Reciter = playerViewModel.playerState.value.reciter
                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            playerViewModel.playerState.value =
                                playerViewModel.playerState.value.copy(reciter = reciters[index]!!, recitationID = null)
                            playerViewModel.fetchMediaUrl()
                            viewModel.onReciterEvents(ReciterEvents.AddReciter(reciters[index]!!))
                        }) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = reciters[index]?.image,
                                contentDescription = "reciter",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                            )
                            if (currentPlayingReciter.arabicName == reciters[index]?.arabicName) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(140.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_graphic_eq_24),
                                        contentDescription = "play",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(30.dp)

                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = reciters[index]!!.arabicName,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                }

            }
            val reciterLoadState = reciters.loadState.refresh
            when {
                reciterLoadState is LoadState.Loading -> {
                    item {
                        Column(
                            modifier = Modifier.padding(24.dp),

                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                reciterLoadState is LoadState.Error -> {
                    val error = reciterLoadState.error
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error.message.toString(),
                                textAlign = TextAlign.Center,
                            )
                            IconButton(onClick = { reciters.retry() }) {
                                Icon(Icons.Outlined.Refresh, contentDescription = "refresh")
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
