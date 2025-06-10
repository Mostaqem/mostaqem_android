package com.mostaqem.features.surahs.presentation.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mostaqem.R
import com.mostaqem.dataStore
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.surahs.data.SortItems
import com.mostaqem.features.surahs.data.SurahSortBy
import com.mostaqem.features.surahs.presentation.SurahsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SurahSortBottomSheet(
    viewModel: SurahsViewModel,
    defaultSortBy: SurahSortBy,
    onBack: () -> Unit,

) {
    val items = listOf<SortItems>(
        SortItems(name = stringResource(R.string.quran), value = SurahSortBy.ID),
        SortItems(name = stringResource(R.string.name), value = SurahSortBy.NAME),
        SortItems(name = stringResource(R.string.revelation_place), value = SurahSortBy.REVELATION_PLACE)
    )
    ModalBottomSheet(
        onDismissRequest = {
            onBack()
        }
    ) {
        LazyColumn {
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.sort_by)) },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
            }
            items(items) {
                ListItem(
                    headlineContent = {
                        Text(it.name)
                    },
                    modifier = Modifier.clickable {
                        viewModel.setSortBy(it.value)
                    },
                    colors = ListItemDefaults.colors(
                        headlineColor = if (defaultSortBy == it.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        containerColor = if (defaultSortBy == it.value) MaterialTheme.colorScheme.primary else Color.Transparent
                    )

                )
            }
        }
    }
}
