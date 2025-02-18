package com.mostaqem.screens.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SurahCard(image: Any, name: String, onClick: () -> Unit,onMoreOptionsClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,

        ) {
        Box(contentAlignment = Alignment.BottomEnd,) {
            AsyncImage(model = image,
                contentDescription = "surah",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .height(170.dp)
                    .width(140.dp)
                    .clickable {
                        onClick()
                    })
            Box(modifier = Modifier.padding(6.0.dp)){
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "play",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .padding(4.dp)


                )
            }
            Box(modifier = Modifier.align(Alignment.TopStart)){
               IconButton(onClick={
                   onMoreOptionsClick()
               }) {
                   Icon(
                       Icons.Default.MoreVert,
                       contentDescription = "more",
                       tint = MaterialTheme.colorScheme.onSurface,
                       modifier = Modifier
                           .clip(RoundedCornerShape(8.dp))
                           .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                           .padding(4.dp)


                   )
               }
            }


        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = name,
            textAlign = TextAlign.Center
        )


    }
}