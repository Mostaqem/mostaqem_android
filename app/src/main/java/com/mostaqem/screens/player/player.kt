package com.mostaqem.screens.player

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mostaqem.R
import com.mostaqem.screens.home.data.player.PlayerSurah
import com.mostaqem.utils.LockScreenOrientation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    sheetState: MutableState<Boolean>,
    playerSurah: PlayerSurah,
    progress: Float,
    onProgressCallback: (Float) -> Unit,
    onPlay: () -> Unit,
    onSeekNext: () -> Unit,
    onSeekPrevious: () -> Unit,
    playerIcon: Int

) {

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    BackHandler(enabled = sheetState.value) {
        sheetState.value = false

    }
    TopAppBar(

        navigationIcon = {
            IconButton(onClick = {
                sheetState.value = false
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_keyboard_arrow_down_24),
                    contentDescription = "back"
                )
            }
        }, title = {})

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = playerSurah.surah?.image,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = playerSurah.surah!!.arabicName,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(text = playerSurah.reciter.arabicName)
        }
        Slider(
            value = progress,
            onValueChange = onProgressCallback,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(text = "2:20")
            Text(text = "4:20")
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledIconButton(onClick = { onSeekPrevious() }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_skip_next_24),
                    contentDescription = "previous"
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            FilledIconButton(
                onClick = {
                    onPlay()
                }, modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
            ) {
                Icon(painter = painterResource(id = playerIcon), contentDescription = "playPause")
            }
            Spacer(modifier = Modifier.width(12.dp))

            FilledIconButton(onClick = { onSeekNext() }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_skip_previous_24),
                    contentDescription = "next"
                )
            }
        }
    }


}