package com.mostaqem.screens.settings.presentation

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mostaqem.BuildConfig
import com.mostaqem.R
import com.mostaqem.core.navigation.models.AppearanceDestination
import com.mostaqem.core.navigation.models.UpdateDestination
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.screens.player.domain.CustomShape
import com.mostaqem.screens.player.domain.Octagon
import com.mostaqem.screens.settings.data.Options
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navController: NavController) {
    var showSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val options = listOf(
        Options(name = "المشغل",
            description = "تغير شكل المشغل و الخ",
            icon = R.drawable.outline_play_arrow_24,
            onClick = {
                navController.navigate(AppearanceDestination)
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CustomShape(Octagon()))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .size(100.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "logo",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(50.dp)
                    )

                }
                Spacer(Modifier.height(16.dp))
                Text(text = "مستقيم", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .size(80.dp)
                            .clickable {
                                val url = "https://github.com/mostaqem/mostaqem_android"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    scope.launch {
                                        SnackbarController.sendEvent(
                                            events = SnackbarEvents(message = "لا يوجد حاجة لفتح اللينك")
                                        )

                                    }

                                }


                            }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.github),
                            contentDescription = "github",
                            modifier = Modifier.size(45.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(18))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .size(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("النسخة")
                            Text(
                                BuildConfig.VERSION_NAME,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                        }

                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(28.dp))
                ) {
                    ListItem(
                        modifier = Modifier.clickable {
                            val url = "https://github.com/Mostaqem/mostaqem_android/issues/new"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            try {
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                scope.launch {
                                    SnackbarController.sendEvent(
                                        events = SnackbarEvents(message = "لا يوجد حاجة لفتح اللينك")
                                    )

                                }

                            }
                        },
                        headlineContent = {
                            Text(
                                "إبلاغ عن مشكلة",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                color = MaterialTheme.colorScheme.primary

                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.github),
                                contentDescription = "person",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        supportingContent = {
                            Text(
                                "اذاننا صاغية",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        },

                        )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {
                            Text(
                                "المطورون",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        supportingContent = {
                            Text(
                                "مازن عمر - عمر صبرة",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(horizontal = 10.dp),
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.Person, contentDescription = "person")
                        },


                        )
                }


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

