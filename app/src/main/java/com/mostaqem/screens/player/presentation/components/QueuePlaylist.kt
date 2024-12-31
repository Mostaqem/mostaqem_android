package com.mostaqem.screens.player.presentation.components

import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.screens.player.data.PlayerSurah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueuePlaylist(modifier: Modifier = Modifier, playlists: List<MediaItem>, player: PlayerSurah) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item { CenterAlignedTopAppBar(title = { Text(text = "تسمع التالي") }) }
        item {

            ListItem(
                headlineContent = { Text(text = player.surah!!.arabicName) },

                leadingContent = {
                    AsyncImage(
                        model = player.surah?.image,
                        contentDescription = "surah",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_graphic_eq_24),
                        contentDescription = "playing"
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,

                    )
            )


        }
        items(playlists) { playlist ->
            val playingQueue: Boolean =
                playlist.mediaMetadata.title.toString() == player.surah?.arabicName

            ListItem(
                headlineContent = { Text(text = playlist.mediaMetadata.title.toString()) },
                leadingContent = {
                    AsyncImage(
                        model = playlist.mediaMetadata.artworkUri,
                        contentDescription = "surah",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                },

                colors = if (playingQueue)
                    ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                else ListItemDefaults.colors(),

                )
        }
    }
}