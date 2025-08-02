package com.mostaqem.features.notifications.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mostaqem.R
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.dataStore
import com.mostaqem.features.notifications.domain.NotificationService
import com.mostaqem.features.notifications.presentation.components.ToggleableNotificationListItem
import com.mostaqem.features.settings.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val viewModel: NotificationViewModel = hiltViewModel()
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        LargeFlexibleTopAppBar(
            title = { Text(stringResource(R.string.notifications)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                }
            },
        )
        val isFridayEnabled =
            context.dataStore.data.collectAsState(initial = AppSettings()).value.fridayNotificationEnabled
        ToggleableNotificationListItem(
            headlineContent = { Text(stringResource(R.string.friday_reminder)) },
            supportingContent = { Text(stringResource(R.string.friday_reminder_description)) },
            isSettingEnabled = isFridayEnabled,
            onToggleSetting = { isEnabled ->
                viewModel.toggleFriday(isEnabled)
            }
        )
        val alMulkEnabled =
            context.dataStore.data.collectAsState(initial = AppSettings()).value.almulkNotificationEnabled
        ToggleableNotificationListItem(
            headlineContent = { Text(stringResource(R.string.nights_reminder)) },
            supportingContent = { Text(stringResource(R.string.nights_reminder_description)) },
            isSettingEnabled = alMulkEnabled,
            onToggleSetting = { isEnabled ->
                viewModel.toggleNights(isEnabled)
            }
        )

    }
}


@Preview
@Composable
private fun NotificationScreenPreview() {
    MostaqemTheme {
        NotificationsScreen(navController = rememberNavController())
    }
}

