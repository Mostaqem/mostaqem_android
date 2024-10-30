package com.mostaqem.screens.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    image: Any,
    playerIcon: Int,
    onPlayPause: () -> Unit,
    surahName: String,
    reciterName: String
) {
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
                    .size(65.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {

                Text(
                    text = surahName,
                    fontWeight = FontWeight.W800,
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = reciterName,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer

                )
            }
        }

        Row {
            IconButton(
                onClick = {
                    onPlayPause()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)

            ) {
                Icon(
                    painter = painterResource(id = playerIcon),
                    contentDescription = "play",
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_skip_previous_24),
                    contentDescription = "play",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

        }

    }

}


@PreviewLightDark
@Composable
private fun Bar() {
    MostaqemTheme {
        PlayerBar(
            image = "",
            playerIcon = R.drawable.outline_play_arrow_24,
            onPlayPause = { /*TODO*/ },
            surahName = "Surah",
            reciterName = "Reciter"
        )
    }


}