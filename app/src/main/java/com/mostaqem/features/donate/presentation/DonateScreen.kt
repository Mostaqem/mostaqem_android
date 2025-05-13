package com.mostaqem.features.donate.presentation

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.player.domain.CustomShape
import com.mostaqem.features.player.domain.Octagon
import com.mostaqem.features.settings.data.AppSettings
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val languageCode =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.language.code

    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LargeTopAppBar(
            title = {
                Text(stringResource(R.string.donate), fontFamily = fontFamily)
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                }
            },
        )

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
            text = stringResource(R.string.thank_you),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = fontFamily
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.thank_you_details),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 53.dp)
        )
        Spacer(Modifier.height(18.dp))

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp),


            ) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .size(130.dp)
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
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.github),
                            contentDescription = "github",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            stringResource(R.string.star_us),
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = fontFamily,
                        )
                    }
                }
            }
            if (isUserFromEgypt(context)) {
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .size(160.dp)
                            .clickable {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://ipn.eg/S/the-sabra/instapay/3LSlWa".toUri()
                                    )
                                        .setPackage("com.egyptianbanks.instapay")
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {

                                    scope.launch {
                                        SnackbarController.sendEvent(
                                            SnackbarEvents(message = "عفوا لا يوجد انستا باي على الجهاز")
                                        )
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center

                    ) {
                        Icon(
                            painter = painterResource(R.drawable.instapay),
                            contentDescription = "instapay",
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .size(130.dp)
                        .clickable {
                            val packageName = "com.mostaqem"
                            val playStoreUri = "market://details?id=$packageName".toUri()
                            val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
                            val webUri =
                                "https://play.google.com/store/apps/details?id=$packageName".toUri()
                            val webIntent = Intent(Intent.ACTION_VIEW, webUri)

                            try {
                                context.startActivity(playStoreIntent)
                            } catch (e: ActivityNotFoundException) {
                                try {
                                    context.startActivity(webIntent)
                                } catch (webEx: ActivityNotFoundException) {
                                    scope.launch {
                                        SnackbarController.sendEvent(
                                            events = SnackbarEvents(message = "لا يوجد حاجة لفتح اللينك")
                                        )

                                    }
                                }


                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.googleplay),
                            contentDescription = "github",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            stringResource(R.string.star_us),
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = fontFamily,
                        )
                    }
                }
            }

        }
    }


}

fun isUserFromEgypt(context: Context): Boolean {
    val country = getCountryFromSim(context)
    return country.equals("EG", ignoreCase = true)
}

@SuppressLint("ServiceCast")
fun getCountryFromSim(context: Context): String? {
    return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        tm.simCountryIso?.takeIf { it.isNotBlank() }?.uppercase()
            ?: tm.networkCountryIso?.takeIf { it.isNotBlank() }?.uppercase()
    } else {
        null
    }
}