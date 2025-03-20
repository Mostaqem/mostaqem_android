package com.mostaqem.features.donate.presentation

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.ui.controller.SnackbarController
import com.mostaqem.core.ui.controller.SnackbarEvents
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.features.player.domain.CustomShape
import com.mostaqem.features.player.domain.Octagon
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LargeTopAppBar(
            title = {
                Text("تبرع")
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

        Text(text = "شكرا لك لأستخدام مستقيم", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(18.dp))
        Text(
            text = "يمكنك التبرع لكي تساعدنا في تحسين البرنامج, او اظهر انك تقدر عملنا و جزاك الله خيرا",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 53.dp)
        )
        Spacer(Modifier.height(18.dp))

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(horizontal =10.dp ),
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
                            "ضع لنا نجمة",
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = kufamFontFamily,
                        )
                    }
                }
            }
            if (isUserFromEgypt()) {
                item {

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))

                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .size(160.dp)
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("InstaPay Username", "the-sabra")
                                clipboard.setPrimaryClip(clip)

                                val intent =
                                    context.packageManager.getLaunchIntentForPackage("com.egyptianbanks.instapay")
                                if (intent != null) {
                                    Toast.makeText(context, "يمكنك لصق الأسم في الدفع", Toast.LENGTH_SHORT).show()
                                    context.startActivity(intent)
                                } else {
                                    scope.launch {
                                        SnackbarController.sendEvent(
                                            SnackbarEvents(message = "عفوا لا يوجد انستا باي على الجهاز")
                                        )
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center

                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.instapay),
                                contentDescription = "instapay",
                                modifier = Modifier.size(100.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text("the-sabra@instapay", textAlign = TextAlign.Center)
                        }


                    }
                }
            }

        }
    }


}

fun isUserFromEgypt(): Boolean {
    val country = Locale.getDefault().country
    return country.equals("EG", ignoreCase = true)
}