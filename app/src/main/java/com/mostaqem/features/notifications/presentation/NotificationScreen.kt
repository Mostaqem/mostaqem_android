package com.mostaqem.features.notifications.presentation

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mostaqem.R
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.dataStore
import com.mostaqem.features.notifications.domain.NotificationService
import com.mostaqem.features.settings.data.AppSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val isNotificationEnabled =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.fridayNotificationEnabled
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
        ListItem(
            headlineContent = { Text(stringResource(R.string.friday_reminder)) },
            supportingContent = { Text(stringResource(R.string.friday_reminder_description)) },
            trailingContent = {
                Switch(
                    checked = isNotificationEnabled,
                    thumbContent = {
                        if (isNotificationEnabled)
                            Icon(
                                Icons.Default.Check, contentDescription = "check",
                                Modifier.size(15.dp)
                            )
                    },
                    onCheckedChange = {
                        viewModel.toggle(it)
                    })
            },

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

