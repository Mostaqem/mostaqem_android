package com.mostaqem.screens.player.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.ui.theme.MostaqemTheme

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    image: Any,
    playerIcon: Int,
    onPlayPause: () -> Unit,
    surahName: String,
    reciterName: String,
    progress: Float
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = image,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = surahName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = reciterName,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
            }
            Row {
                IconButton(
                    onClick = {
                        onPlayPause()
                    }) {
                    Icon(
                        painter = painterResource(id = playerIcon),
                        contentDescription = "play",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
//            IconButton(
//                onClick = {},
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.outline_skip_previous_24),
//                    contentDescription = "play",
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            }

            }

        }

        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())

    }

}


@PreviewLightDark
@Composable
private fun PreviewBar() {
    MostaqemTheme {
        PlayerBar(
            image = "",
            playerIcon = R.drawable.outline_play_arrow_24,
            onPlayPause = { /*TODO*/ },
            surahName = "Surah",
            reciterName = "Reciter",
            progress = 0.5f
        )
    }

}