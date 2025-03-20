package com.mostaqem.features.settings.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mostaqem.BuildConfig
import com.mostaqem.R
import com.mostaqem.core.navigation.models.AppearanceDestination
import com.mostaqem.core.navigation.models.OfflineSettingsDestination
import com.mostaqem.core.navigation.models.UpdateDestination
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.features.about.presentation.AboutScreen
import com.mostaqem.features.player.domain.CustomShape
import com.mostaqem.features.player.domain.Octagon
import com.mostaqem.features.settings.data.Options
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navController: NavController) {
    var showSheet by remember { mutableStateOf(false) }

    val options = listOf(
        Options(name = "المشغل",
            description = "تغير شكل المشغل و الخ",
            icon = R.drawable.outline_play_arrow_24,
            onClick = {
                navController.navigate(AppearanceDestination)
            }),
        Options(name = "اوفلاين",
            description = "تغير طريقة تشغيل اوفلاين , عرض المساحة",
            icon = R.drawable.outline_folder,
            onClick = {
                navController.navigate(OfflineSettingsDestination)
            }),
        Options(name = "تحديث",
            description = "التحقق من التحديث",
            icon = R.drawable.outline_update_24,
            onClick = {
                navController.navigate(UpdateDestination)
            }),
        Options(name = "عن مستقيم",
            description = "النسخة , من نحن",
            icon = R.drawable.info_outline,
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
            AboutScreen(navController=navController){
                showSheet = false
            }
        }
    }

    LazyColumn {
        item {
            LargeTopAppBar(title = {
                Text(
                    text = "اعدادات", style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = kufamFontFamily, fontWeight = FontWeight.Medium
                    )
                )
            })
        }
        items(options) {
            ListItem(modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(120.dp)
                .clickable { it.onClick() },
                leadingContent = {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = it.name,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                }, headlineContent = {
                    Text(
                        text = it.name, style = MaterialTheme.typography.titleLarge
                    )
                }, supportingContent = {
                    Text(
                        text = it.description, style = MaterialTheme.typography.titleMedium
                    )
                })

        }
        item { Spacer(Modifier.height(100.dp)) }


    }

}

@Preview
@Composable
private fun UpdateListItem() {
    ListItem(modifier = Modifier
        .fillMaxWidth()
        .requiredHeight(120.dp),
        leadingContent = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_update_24),
                    contentDescription = "it.name",
                    modifier = Modifier.size(25.dp)
                )
            }

        }, headlineContent = {
            Text(
                text = "تحديث", style = MaterialTheme.typography.titleLarge
            )
        }, supportingContent = {
            Text(
                text = "السمات، عناصر التحكم الإضافية، عرض المحتوى",
                style = MaterialTheme.typography.titleMedium
            )
        })
}

