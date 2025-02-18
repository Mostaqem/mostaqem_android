package com.mostaqem.screens.settings.presentation.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.screens.player.domain.CustomShape
import com.mostaqem.screens.player.domain.Octagon
import com.mostaqem.screens.settings.presentation.UpdateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(
    modifier: Modifier = Modifier,
    viewModel: UpdateViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    viewModel.checkForUpgrade(pInfo.versionName.toString())

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                }
            },
            title = {

            },
        )

        val showUpgrade by viewModel.showUpgradePrompt.collectAsState()

        if (!showUpgrade) {
            Text("لا يوجد تحديث", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {

            UpdateButton(viewModel, context)
        }


    }

}

@Composable
private fun UpdateButton(
    viewModel: UpdateViewModel, context: Context
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .height(90.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "info",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    "التحديث يحدث فقط عندما تتصل بال Wifi",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

        }
        Spacer(Modifier.height(30.dp))

        Text(
            "النسخة الجديدة",
            fontFamily = kufamFontFamily,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(30.dp))
        val latest by viewModel.newVersion.collectAsState()

        latest?.let {
            Text(
                it,
                fontFamily = kufamFontFamily,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(30.dp))
        val infiniteTransition = rememberInfiniteTransition()
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000, easing = LinearEasing
                ), repeatMode = RepeatMode.Restart
            )
        )
        var isDownloading by remember { mutableStateOf(false) }
        Box(contentAlignment = Alignment.Center) {
            ElevatedButton(
                modifier = Modifier
                    .rotate(rotationAngle)
                    .clip(CustomShape(Octagon()))
                    .size(200.dp),
                onClick = {
                    if (!isDownloading) {
                        isDownloading = true
                        viewModel.performUpgrade(context)
                    }
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = animateColorAsState(
                        targetValue = if (isDownloading) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Unspecified
                        }, animationSpec = tween(durationMillis = 500)
                    ).value
                )

            ) {}
            Text(
                if (isDownloading) "ثوان.." else "تحديث",
                style = MaterialTheme.typography.titleLarge,
                color = if (isDownloading) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
            )

        }


    }
}