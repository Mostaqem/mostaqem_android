package com.mostaqem.features.player.presentation.components.sleep

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.R
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.settings.data.AppSettings

@Composable
fun SleepDialog(
    modifier: Modifier = Modifier,
    showSleepDialog: MutableState<Boolean>,
    playerViewModel: PlayerViewModel,
    viewModel: SleepViewModel,
    remainingTime: Long,
) {
    val min = 5
    val max = 60
    val step = 5
    val steps = (max - min) / step
    var sliderPosition by remember { mutableFloatStateOf((5 - min) / step.toFloat()) }

    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val durationChapter by playerViewModel.duration.collectAsState()

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }
    val selectedMin = (sliderPosition * step).toInt() + min
    val isArabic = languageCode == "ar"
    val languageSelectedMin = if (isArabic) selectedMin.toArabicNumbers() else selectedMin
    Dialog(onDismissRequest = { showSleepDialog.value = false }) {

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                stringResource(R.string.sleep_timer),
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                fontFamily = fontFamily

            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                if (isRunning) {
                    Text(remainingTime.toMinSec(), fontSize = 45.sp, fontFamily = fontFamily)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = {
                            viewModel.cancelTimer()
                            showSleepDialog.value = false
                        }) {
                            Text(stringResource(R.string.stop))
                        }
                        Button(onClick = { showSleepDialog.value = false }) {
                            Text(stringResource(R.string.done))
                        }
                    }
                } else {
                    Text(
                        "$languageSelectedMin ${
                            stringResource(
                                R.string.min
                            )
                        }",
                        fontSize = 45.sp,
                        fontFamily = fontFamily
                    )
                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                        },
                        steps = steps - 1,
                        valueRange = 0f..steps.toFloat(),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(onClick = {
                        viewModel.startTimer(durationChapter) {
                            playerViewModel.pause()
                        }
                        showSleepDialog.value = false

                    }) {
                        Text(stringResource(R.string.end_of_chapter))
                    }
                    Spacer(Modifier.height(15.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = {
                            showSleepDialog.value = false

                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button(onClick = {

                            viewModel.startTimer(selectedMin * 60_000L) {
                                playerViewModel.pause()
                            }
                            showSleepDialog.value = false
                        }, Modifier.width(150.dp)) {
                            Text(stringResource(R.string.start))
                        }
                    }
                }
            }

        }
    }
}

fun Long.toMinSec(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

