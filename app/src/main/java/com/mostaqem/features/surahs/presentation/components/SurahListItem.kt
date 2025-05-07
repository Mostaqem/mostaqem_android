package com.mostaqem.features.surahs.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.core.database.events.SurahEvents
import com.mostaqem.features.surahs.data.Surah

@Composable
fun SurahListItem(
    modifier: Modifier = Modifier,
    surah: Surah,
    isArabic: Boolean,
    isCurrentSurahPlayed: Boolean,
    onMenu: () -> Unit,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = if (isArabic) surah.arabicName else surah.complexName)
        },
        supportingContent = { Text(text = if (isArabic) surah.complexName else surah.arabicName) },
        leadingContent = {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = surah.image,
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

                onMenu()

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
        modifier = modifier.clickable {
//            playerViewModel.changeSurah(surahs[index])
//            playerViewModel.fetchMediaUrl()
//            viewModel.onSurahEvents(SurahEvents.AddSurah(surahs[index]))
            onClick()
        })
}