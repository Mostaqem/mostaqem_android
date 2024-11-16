package com.mostaqem.screens.player.presentation

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.mostaqem.screens.player.data.PlayerSurah
import com.mostaqem.screens.reciters.presentation.ReciterScreen
import com.mostaqem.utils.LockScreenOrientation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    playerSurah: PlayerSurah,
    progressPercentage: Float,
    onProgressCallback: (Float) -> Unit,
    onPlay: () -> Unit,
    onSeekNext: () -> Unit,
    progress: Float,
    onSeekPrevious: () -> Unit,
    playerIcon: Int,
    duration: Float

) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val reciterSheetState = rememberModalBottomSheetState(
    )
    var isReciterSheetShown by rememberSaveable() {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(vertical = 40.dp)) {
            AsyncImage(
                model = playerSurah.surah?.image,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
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
                value = progressPercentage,
                onValueChange = onProgressCallback,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = progress.toHoursMinutesSeconds())
                Text(text = duration.toHoursMinutesSeconds())
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onSeekPrevious() }) {
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
                    Icon(
                        painter = painterResource(id = playerIcon), contentDescription = "playPause"
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                IconButton(onClick = { onSeekNext() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_skip_previous_24),
                        contentDescription = "next"
                    )
                }
            }
        }
        if (isReciterSheetShown) {
            ModalBottomSheet(sheetState = reciterSheetState,
                dragHandle = {},
                containerColor = MaterialTheme.colorScheme.surface,
                onDismissRequest = {
                    isReciterSheetShown = false
                }) {
                ReciterScreen()
            }
        }


        Row(
            Modifier
                .padding(10.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                isReciterSheetShown = true
            }) {
                Icon(Icons.Outlined.Person, contentDescription = "reciter")
            }
            Spacer(modifier = Modifier.width(15.dp))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_book_24),
                    contentDescription = "read"
                )
            }
        }
    }


}


