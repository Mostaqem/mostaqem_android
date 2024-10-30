package com.mostaqem.screens.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SurahCard(image: Any, name: String, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,

        ) {
        AsyncImage(model = image,
            contentDescription = "surah",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .height(170.dp)
                .width(140.dp)
                .clickable {
                    onClick()


                })
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = name,
            textAlign = TextAlign.Center
        )


    }
}