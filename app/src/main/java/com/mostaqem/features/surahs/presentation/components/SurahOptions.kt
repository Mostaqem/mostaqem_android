package com.mostaqem.features.surahs.presentation.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.navigation.models.ReadingDestination
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reciters.data.reciter.Reciter
import com.mostaqem.features.surahs.data.Surah

@Composable
fun SurahOptions(
    selectedSurah: Surah?,
    selectedReciter: Reciter? = null,
    selectedRecitationID: Int? = null,
    playerViewModel: PlayerViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
) {
    LazyColumn {
        item {
            ListItem(
                headlineContent = { Text(text = selectedSurah!!.arabicName) },
                supportingContent = { selectedReciter?.arabicName?.let { Text(text = it) } },
                leadingContent = {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = selectedSurah!!.image,
                            contentDescription = "surah",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
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
                headlineContent = { Text(text = "تشغيل التالي") },
                modifier = Modifier.clickable {

                    playerViewModel.addNext(
                        selectedSurah!!.id,
                        selectedReciter?.id,
                        selectedRecitationID
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
                headlineContent = { Text(text = "أضف إلى قائمة التشغيل") },
                modifier = Modifier.clickable {

                    playerViewModel.addMediaItem(
                        selectedSurah!!.id,
                        selectedReciter?.id,
                        selectedRecitationID
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
                headlineContent = { Text(text = "اقرأ السورة") },
                modifier = Modifier.clickable {
                    val surahID = selectedSurah!!.id
                    navController.navigate(
                        ReadingDestination(
                            surahID = surahID,
                            surahName = selectedSurah.arabicName
                        )
                    )
                    onDismiss()

                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)

            )
        }
    }
}