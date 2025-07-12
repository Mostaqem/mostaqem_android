package com.mostaqem.features.history.presentation.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.features.history.data.AllFilter
import com.mostaqem.features.history.data.ChapterFilter
import com.mostaqem.features.history.data.RecitersFilter
import com.mostaqem.features.history.data.SearchFilter
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.player.data.PlayerSurah
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah
import com.mostaqem.features.surahs.presentation.components.SurahListItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearch(
    modifier: Modifier = Modifier,
    query: String,
    selectedLabel: SearchFilter = AllFilter(),
    queryList: List<SearchFilter> = emptyList(),
    onSelect: (SearchFilter) -> Unit,
    onHideBottom: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    isArabic: Boolean = false,
    player: PlayerSurah?,
    defaultReciterName: String,
    onReciterclick: (Reciter) -> Unit,
    onSurahMenuClick: (Surah) -> Unit,
    onSurahClick: (Surah) -> Unit,

    ) {

    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                        onSearchQueryChange(it)
                    },
                    onSearch = {
                        expanded = false
                    },
                    placeholder = { Text(stringResource(R.string.search_reciters_chapters)) },
                    expanded = expanded,
                    onExpandedChange = {

                        expanded = it
                        onHideBottom(it)
                    },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(onClick = {
                                onSearchQueryChange("")
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
                                onSearchQueryChange("")
                                expanded = false
                                onHideBottom(false)
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
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                AssistLabels(selectedLabel = selectedLabel, onSelect = onSelect)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = queryList,
                        key = { item ->
                            when (item) {
                                is AllFilter -> item.id
                                is ChapterFilter -> item.id
                                is RecitersFilter -> item.id
                            }
                        }) { item ->
                        when (item) {
                            is ChapterFilter -> {
                                if (item.chapters.isNotEmpty()) {
                                    val name =
                                        if (item.chapters.size > 1) stringResource(R.string.surahs) else stringResource(
                                            R.string.surah
                                        )
                                    val count =
                                        if (isArabic) item.chapters.size.toArabicNumbers() else item.chapters.size
                                    Text(
                                        "$count $name",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    item.chapters.forEach {
                                        val currentPlayedSurah = player?.surah
                                        val isCurrentSurahPlayed: Boolean =
                                            currentPlayedSurah != null && (currentPlayedSurah.arabicName == it.complexName || currentPlayedSurah.arabicName == it.arabicName)
                                        SurahListItem(
                                            surah = it,
                                            onClick = { onSurahClick(it) },
                                            onMenu = { onSurahMenuClick(it) },
                                            isCurrentSurahPlayed = isCurrentSurahPlayed,
                                            isArabic = isArabic,
                                            defaultReciterName = defaultReciterName
                                        )
                                    }
                                }

                            }

                            is RecitersFilter -> {
                                if (item.reciters.isNotEmpty()) {
                                    val name =
                                        if (item.reciters.size > 1) stringResource(R.string.reciters) else stringResource(
                                            R.string.reciter
                                        )
                                    val count =
                                        if (isArabic) item.reciters.size.toArabicNumbers() else item.reciters.size

                                    Text(
                                        "$count $name",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    item.reciters.forEach {
                                        ListItem(
                                            headlineContent = {
                                                Text(if (isArabic) it.arabicName else it.englishName)
                                            },
                                            leadingContent = {
                                                AsyncImage(
                                                    model = it.image,
                                                    contentDescription = "reciter",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(60.dp)
                                                        .clip(RoundedCornerShape(18))
                                                )
                                            },
                                            colors = ListItemDefaults.colors(
                                                containerColor = Color.Transparent
                                            ),
                                            modifier = Modifier.clickable {
                                                onReciterclick(it)
                                            }
                                        )
                                    }
                                }
                            }

                            else -> null
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun AssistLabels(
    modifier: Modifier = Modifier,
    selectedLabel: SearchFilter,
    onSelect: (SearchFilter) -> Unit
) {
    val labels =
        listOf<SearchFilter>(
            AllFilter(displayName = stringResource(R.string.all)),
            ChapterFilter(id = "chapters", displayName = stringResource(R.string.surahs)),
            RecitersFilter(id = "reciters", displayName = stringResource(R.string.reciters))
        )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(15.dp), modifier = modifier) {
        items(labels) { label ->
            FilterChip(
                onClick = {
                    onSelect(label)
                },
                selected = selectedLabel.id == label.id,
                shape = RoundedCornerShape(20),
                label = {
                    Text(
                        label.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )
                },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedLabel.id == label.id
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )

            )
        }
    }
}


@Preview
@Composable
private fun AdvancedSearchPreview() {
    MostaqemTheme {
        AdvancedSearch(
            onSearchQueryChange = {},
            onHideBottom = {},
            onSelect = {},
            query = "",
            isArabic = false,
            player = null,
            onSurahMenuClick = {},
            onSurahClick = {},
            onReciterclick = {},
            defaultReciterName = ""
        )
    }

}

