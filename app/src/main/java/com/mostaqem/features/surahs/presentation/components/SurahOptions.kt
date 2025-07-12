package com.mostaqem.features.surahs.presentation.components

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.navigation.models.ReadingDestination
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah

@OptIn(UnstableApi::class)
@Composable
fun SurahOptions(
    selectedSurah: String?,
    selectedSurahID: Int,
    context: Context,
    selectedReciter: String? = null,
    selectedReciterID: Int? =null,
    selectedRecitation: Int? = null,
    playerViewModel: PlayerViewModel,
    navController: NavController,
    onDismiss: () -> Unit,


    ) {
    val defaultReciter by playerViewModel.defaultReciterState.collectAsState()
    val defaultRecitationID by playerViewModel.defaultRecitationID.collectAsState()
    val isArabic = MaterialTheme.typography.titleLarge.fontFamily == kufamFontFamily
    LazyColumn {
        item {
            ListItem(
                headlineContent = {
                    if (selectedSurah != null) {
                        Text(selectedSurah)
                    }
                },
                supportingContent = {
                    Text(
                        selectedReciter
                            ?: if (isArabic) defaultReciter.arabicName else defaultReciter.englishName
                    )
                },

                leadingContent = {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = "https://img.freepik.com/premium-photo/illustration-mosque-with-crescent-moon-stars-simple-shapes-minimalist-flat-design_217051-15556.jpg",
                            contentDescription = "surah",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                },
                colors =
                    ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
            HorizontalDivider()
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_east_24),
                        contentDescription = "play_next"
                    )
                },
                headlineContent = { Text(text = stringResource(R.string.play_next)) },
                modifier = Modifier.clickable {

                    playerViewModel.addNext(
                        selectedSurahID,
                        selectedReciterID ?: defaultReciter.id,
                        selectedRecitation ?: defaultRecitationID,
                        context
                    )

                    onDismiss()
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
        }
        item {
            ListItem(
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_library_add_24),
                        contentDescription = "add_queue_item"
                    )
                },
                headlineContent = { Text(text = stringResource(R.string.add_queue)) },
                modifier = Modifier.clickable {

                    playerViewModel.addMediaItem(
                        selectedSurahID,
                        selectedReciterID ?: defaultReciter.id,
                        selectedRecitation ?: defaultRecitationID,
                        context
                    )
                    onDismiss()
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)

            )
        }
        item { HorizontalDivider() }
        item {
            ListItem(
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_menu_book_24),
                        contentDescription = "read"
                    )
                },
                headlineContent = { Text(text = stringResource(R.string.read_chapter)) },
                modifier = Modifier.clickable {
                    selectedSurah?.let {
                        navController.navigate(
                            ReadingDestination(
                                surahID = selectedSurahID,
                                surahName = it
                            )
                        )
                    }
                    onDismiss()

                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)

            )
        }
    }
}