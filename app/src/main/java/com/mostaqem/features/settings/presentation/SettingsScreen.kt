package com.mostaqem.features.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.models.AppearanceDestination
import com.mostaqem.core.navigation.models.LanguagesDestination
import com.mostaqem.core.navigation.models.NotificationsDestination
import com.mostaqem.core.navigation.models.OfflineSettingsDestination
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.about.presentation.AboutScreen
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.settings.data.Options

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var showSheet by remember { mutableStateOf(false) }

    val options = listOf(
        Options(
            name = stringResource(R.string.language),
            description = stringResource(R.string.change_language),
            icon = Icons.Outlined.Translate,
            onClick = {
                navController.navigate(LanguagesDestination)
            }),
        Options(
            name = stringResource(R.string.notifications),
            description = stringResource(R.string.notifications_desc),
            icon = Icons.Outlined.Notifications,
            onClick = {
                navController.navigate(NotificationsDestination)
            }),
        Options(
            name = stringResource(R.string.player),
            description = stringResource(R.string.player_decsription),
            icon = Icons.Outlined.PlayArrow,
            onClick = {
                navController.navigate(AppearanceDestination)
            }),

        Options(
            name = stringResource(R.string.offline),
            description = stringResource(R.string.offline_description),
            icon = Icons.Outlined.Folder,
            onClick = {
                navController.navigate(OfflineSettingsDestination)
            }),

        Options(
            name = stringResource(R.string.about),
            description = stringResource(R.string.about_description),
            icon = Icons.Outlined.Info,
            onClick = {
                showSheet = true
            }),

        )
    if (showSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = {
                showSheet = false
            }) {
            AboutScreen(navController = navController) {
                showSheet = false
            }
        }
    }
    val languageCode =
        LocalContext.current.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }

    LazyColumn {
        item {
            LargeTopAppBar(title = {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = fontFamily, fontWeight = FontWeight.Medium
                    )
                )
            })
        }
        items(options) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(120.dp)
                    .clickable { it.onClick() },
                leadingContent = {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            it.icon,
                            contentDescription = it.name,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                }, headlineContent = {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = fontFamily
                    )
                }, supportingContent = {
                    Text(
                        text = it.description, style = MaterialTheme.typography.titleSmall,
                    )
                })

        }
        item { Spacer(Modifier.height(100.dp)) }


    }

}


