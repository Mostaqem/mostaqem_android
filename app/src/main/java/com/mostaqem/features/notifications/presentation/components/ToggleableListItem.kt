package com.mostaqem.features.notifications.presentation.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mostaqem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToggleableNotificationListItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable () -> Unit,
    isSettingEnabled: Boolean,
    onToggleSetting: (Boolean) -> Unit
) {
    val context = LocalContext.current

    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                onToggleSetting(true)
            } else {
                Toast.makeText(
                    context,
                    R.string.notification_permission,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    val isNotificationActuallyEnabled = hasNotificationPermission && isSettingEnabled

    ListItem(
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = {
            Switch(
                checked = isNotificationActuallyEnabled,
                thumbContent = {
                    if (isNotificationActuallyEnabled) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "checked", // Localize this
                            modifier = Modifier.size(15.dp)
                        )
                    }
                },
                onCheckedChange = { desiredState ->
                    if (desiredState) { // User wants to turn it ON
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!hasNotificationPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onToggleSetting(true)
                            }
                        } else {
                            onToggleSetting(true)
                        }
                    } else {
                        onToggleSetting(false)
                    }
                }
            )
        }
    )
}
