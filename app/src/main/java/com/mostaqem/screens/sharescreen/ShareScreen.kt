package com.mostaqem.screens.sharescreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.screens.player.presentation.PlayerViewModel
import com.mostaqem.screens.reading.presentation.displayVerseNumber
import com.mostaqem.screens.settings.domain.toArabicNumbers
import com.mostaqem.screens.sharescreen.data.SelectedColor
import com.mostaqem.screens.sharescreen.data.SelectedFont
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    modifier: Modifier = Modifier,
    shareViewModel: ShareViewModel,
    hidePlayerState: MutableState<Boolean>,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    chapterName: String
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var composableBounds by remember { mutableStateOf<Rect?>(null) }
    val context = LocalContext.current
    var isButtonVisible by remember { mutableStateOf(true) }
    val verses by shareViewModel.verses.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val colors =
        listOf(
            SelectedColor(
                background = colorScheme.secondaryContainer,
                onBackground = colorScheme.onSecondaryContainer
            ),
            SelectedColor(background = Color(0xff7e9680), onBackground = Color.Black),
            SelectedColor(background = Color(0xffeab595), onBackground = Color.Black),
            SelectedColor(background = Color(0xff79616f), onBackground = Color.White),
            SelectedColor(background = Color(0xffd87f81), onBackground = Color.White),
        )
    var selectedColor by remember { mutableStateOf<Color>(colors.first().background) }
    var onSelectedColor by remember { mutableStateOf<Color>(colors.first().onBackground) }
    var showEditSheet by remember { mutableStateOf(false) }
    val availableFonts = listOf(
        SelectedFont(
            font = Font(R.font.uthmani),
            name = "عثماني",
            fontSize = 30.sp,
            lineHeight = 45.sp
        ),
        SelectedFont(
            font = Font(R.font.amiri),
            name = "أميري",
            fontSize = 25.sp,
            lineHeight = 50.sp
        )
    )
    var selectedFont by remember { mutableStateOf(availableFonts.first()) }
    val view = LocalView.current
    val annotatedString = buildAnnotatedString {
        verses.forEach { verse ->
            withStyle(
                style = SpanStyle(
                )
            ) {
                append(verse.text)
            }
            withStyle(
                style = SpanStyle(
                    letterSpacing = (-0.5).sp
                )
            ) {
                if (selectedFont.name == "عثماني") {
                    append(" ${displayVerseNumber(verse.verse)} ")
                } else {
                    append(" \u06DD${verse.verse.toArabicNumbers()} ")
                }
            }
        }
    }

    Log.d("Hide Player", "Hide Player: ${hidePlayerState}")

    LaunchedEffect(isButtonVisible) {
        hidePlayerState.value = true
        playerViewModel.pause()
        if (!isButtonVisible) {
            delay(100)

            composableBounds?.let { bounds ->
                val screenBitmap = view.drawToBitmap()
                bitmap = try {
                    Bitmap.createBitmap(
                        screenBitmap,
                        bounds.left.toInt(),
                        bounds.top.toInt(),
                        bounds.width.toInt(),
                        bounds.height.toInt()
                    )
                } catch (e: Exception) {
                    null
                }

                if (!isButtonVisible) {
                    bitmap?.let { shareBitmap(context, it) }

                }
            }
            isButtonVisible = true

        }
    }


    Box(contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(selectedColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        hidePlayerState.value = false
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back",
                            tint = onSelectedColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditSheet = true }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = onSelectedColor
                        )
                    }
                    IconButton(onClick = {
                        isButtonVisible = false

                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "share",
                            tint = onSelectedColor
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = selectedColor)
            )
            if (showEditSheet) {
                ModalBottomSheet(onDismissRequest = { showEditSheet = false }) {
                    Column(
                        Modifier.padding(bottom = 16.dp, start = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text("تغير اللون", fontSize = 20.sp)
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            items(colors) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(it.background)
                                        .then(
                                            if (selectedColor == it.background) {
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )

                                        .clickable {
                                            selectedColor = it.background
                                            onSelectedColor = it.onBackground
                                        })
                            }
                        }
                        Text("تغير الخط", fontSize = 20.sp)

                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            items(availableFonts) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .then(
                                            if (selectedFont == it) {
                                                Modifier
                                                    .border(
                                                        width = 2.dp,
                                                        color = Color.White,
                                                        shape = RoundedCornerShape(18.dp)
                                                    )
                                                    .padding(20.dp)
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable {
                                            selectedFont = it
                                        }) {
                                    Text(it.name, fontSize = 16.sp)
                                }
                            }
                        }

                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        composableBounds = coordinates.boundsInRoot()
                    }
                    .fillMaxHeight()
                    .padding(horizontal = 15.dp)
            ) {
                Image(
                    painter = painterResource(if (verses.first().chapter != 9) R.drawable.basmallah else R.drawable.a3ooz),
                    contentDescription = "basmallah",
                    modifier = Modifier.width(250.dp),
                    colorFilter = ColorFilter.tint(onSelectedColor),
                )
                Text(
                    text = annotatedString,
                    lineHeight = selectedFont.lineHeight,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Justify,
                    color = onSelectedColor,
                    fontSize = selectedFont.fontSize,
                    fontFamily = selectedFont.font.toFontFamily(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)

                )
                Spacer(Modifier.height(15.dp))

                Text(
                    "- $chapterName -",
                    fontFamily = selectedFont.font.toFontFamily(),
                    fontWeight = FontWeight.Bold,
                    color = onSelectedColor
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(onSelectedColor)
                            .padding(5.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.logo),
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterEnd),
                            contentDescription = "logo",
                            tint = selectedColor
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "مستقيم",
                        fontFamily = kufamFontFamily,
                        fontSize = 16.sp,
                        color = onSelectedColor
                    )
                }


            }


        }


    }


}

fun shareBitmap(context: Context, bitmap: Bitmap) {
    val cachePath = File(context.externalCacheDir, "shared_image.jpg")
    FileOutputStream(cachePath).use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    }

    // Get a content URI using FileProvider
    val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // Use your FileProvider authority
        cachePath
    )

    // Create share intent
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Launch the share dialog
    context.startActivity(Intent.createChooser(intent, "Share Image"))
}