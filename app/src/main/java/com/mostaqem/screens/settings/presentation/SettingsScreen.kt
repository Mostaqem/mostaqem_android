package com.mostaqem.screens.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.navigation.AppearanceDestination
import com.mostaqem.core.navigation.UpdateDestination
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.screens.settings.data.Options

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navController: NavController) {
    val options = listOf<Options>(
        Options(name = "التشغيل",
            description = "السمات، عناصر التحكم الإضافية، عرض المحتوى",
            icon = R.drawable.outline_play_arrow_24,
            onClick = {
                navController.navigate(AppearanceDestination)
            }),

        Options(name = "تحديث",
            description = "التحقق من التحديث",
            icon = R.drawable.outline_update_24,
            onClick = {
                navController.navigate(UpdateDestination)
            })
    )
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
                text =  "تحديث", style = MaterialTheme.typography.titleLarge
            )
        }, supportingContent = {
            Text(
                text = "السمات، عناصر التحكم الإضافية، عرض المحتوى", style = MaterialTheme.typography.titleMedium
            )
        })
}

