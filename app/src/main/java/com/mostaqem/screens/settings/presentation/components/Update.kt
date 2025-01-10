package com.mostaqem.screens.settings.presentation.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mostaqem.core.ui.theme.kufamFontFamily
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
        LargeTopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back ")
                }
            },
            title = {
                Text(
                    text = "تحديث", style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = kufamFontFamily, fontWeight = FontWeight.Medium
                    )
                )
            },
        )
        val showUpgrade by viewModel.showUpgradePrompt.collectAsState()


        if (showUpgrade) {
            ElevatedButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp),
                onClick = {
                    viewModel.performUpgrade(context)
                }) {
                Text("تنزيل", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(30.dp))
            val latest by viewModel.newVersion.collectAsState()

            Text("${latest}هناك تحديث الجديد, نسخة:")

        } else {
            Text("لا يوجد تحديث", modifier = Modifier.align(Alignment.CenterHorizontally))

        }


    }


}