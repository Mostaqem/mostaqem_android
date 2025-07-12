package com.mostaqem.features.about.presentation

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mostaqem.BuildConfig
import com.mostaqem.R
import com.mostaqem.core.navigation.models.DonationDestination
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.features.player.domain.CustomShape
import com.mostaqem.features.player.domain.Octagon
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.settings.data.AppSettings

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
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
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = MaterialTheme.typography.titleLarge.fontFamily
        )
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
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
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
                    Text(stringResource(R.string.version))
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
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
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
                        stringResource(R.string.bug_report),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = MaterialTheme.typography.titleLarge.fontFamily

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
                        stringResource(R.string.bug_report_details),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                },

                )
            HorizontalDivider()
            ListItem(
                modifier = Modifier.clickable {
                    onBack()
                    navController.navigate(DonationDestination)
                },
                headlineContent = {
                    Text(
                        stringResource(R.string.donate),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = MaterialTheme.typography.titleLarge.fontFamily
                    )
                },
                supportingContent = {
                    Text(
                        stringResource(R.string.donate_details),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(R.drawable.donate),
                        contentDescription = "donate"
                    )
                },
            )
        }


    }

}
