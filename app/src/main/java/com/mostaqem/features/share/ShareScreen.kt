package com.mostaqem.features.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.ui.theme.chapterCaliFontFamily
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.core.ui.theme.productFontFamily
import com.mostaqem.dataStore
import com.mostaqem.features.offline.domain.toArabicNumbers
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.reading.presentation.displayVerseNumber
import com.mostaqem.features.settings.data.AppSettings
import com.mostaqem.features.share.data.SelectedColor
import com.mostaqem.features.share.data.SelectedFont
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShareScreen(
    modifier: Modifier = Modifier,
    shareViewModel: ShareViewModel,
    navController: NavController,
    playerViewModel: PlayerViewModel,
    chapterNumber: String,


    ) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var composableBounds by remember { mutableStateOf<Rect?>(null) }
    val context = LocalContext.current
    var isButtonVisible by remember { mutableStateOf(true) }
    val verses by shareViewModel.verses.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val colors = listOf(
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
    var showColorsDialog by rememberSaveable { mutableStateOf(false) }

    val availableFonts = listOf(
        SelectedFont(
            font = Font(R.font.uthmani),
            name = stringResource(R.string.uthmani),
            fontSize = 30.sp,
            lineHeight = 45.sp
        ), SelectedFont(
            font = Font(R.font.amiri),
            name = stringResource(R.string.amiri),
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
                if (selectedFont.name == stringResource(R.string.uthmani)) {
                    append(" ${displayVerseNumber(verse.verse)} ")
                } else {
                    append(" \u06DD${verse.verse.toArabicNumbers()} ")
                }
            }
        }
    }
    var selectedText by remember { mutableStateOf(TextFieldValue(annotatedString)) }

    val languageCode =
        context.dataStore.data.collectAsState(initial = AppSettings()).value.language.code
    val fontFamily = remember(languageCode) {
        if (languageCode == "en") productFontFamily else kufamFontFamily
    }

    LaunchedEffect(isButtonVisible) {
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

    var expanded by rememberSaveable { mutableStateOf(true) }
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (isButtonVisible) {
                HorizontalFloatingToolbar(

                    expanded = expanded,

                    ) {
                    ToggleButton(
                        checked = showColorsDialog,
                        shapes = ToggleButtonDefaults.shapes(checkedShape = RoundedCornerShape(26.dp)),
                        onCheckedChange = {
                            showColorsDialog = it
                        }) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Icon(Icons.Default.FormatPaint, contentDescription = "adjust color")
                            Spacer(Modifier.width(6.dp))
                            Text("Colors")

                        }
                    }



                }
            }


        }) { paddingValues ->
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.padding(paddingValues)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(selectedColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    TopAppBar(
                        title = { Text("Sharing") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "back"
                                )
                            }
                        },
                        actions = {

                            FilledIconButton(
                                shapes = IconButtonDefaults.shapes(),
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .size(
                                        IconButtonDefaults.smallContainerSize(
                                            IconButtonDefaults.IconButtonWidthOption.Wide
                                        )
                                    ),
                                onClick = {
                                    isButtonVisible = false

                                }) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "share",
                                )
                            }

                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = selectedColor)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                composableBounds = coordinates.boundsInRoot()
                            }
                            .fillMaxHeight()
                            .padding(horizontal = 15.dp)) {

                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "118",
                                    fontFamily = chapterCaliFontFamily,
                                    fontSize = 95.sp,
                                    textAlign = TextAlign.Center,
                                    color = onSelectedColor
                                )
                                Text(
                                    text = chapterNumber,
                                    fontFamily = chapterCaliFontFamily,
                                    fontSize = 35.sp,
                                    color = onSelectedColor,
                                    fontWeight = FontWeight.W500
                                )
                            }

                            Text(
                                text = selectedText.annotatedString,
                                lineHeight = selectedFont.lineHeight,
                                textAlign = TextAlign.Justify,
                                color = onSelectedColor,
                                fontSize = selectedFont.fontSize,
                                fontFamily = selectedFont.font.toFontFamily(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)

                            )
                        }


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
                                stringResource(R.string.app_name),
                                fontFamily = fontFamily,
                                fontSize = 16.sp,
                                color = onSelectedColor
                            )
                        }


                    }


                }
            }

            if (isButtonVisible) {
                AnimatedVisibility(
                    visible = showColorsDialog,
                    enter = slideInVertically(
                        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                    ) + fadeIn(
                        animationSpec = tween(300)
                    ),
                    exit = slideOutVertically(
                        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()

                    ) + fadeOut(
                        animationSpec = tween(200)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 90.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(20.dp)
                        ) {
                            items(colors) { color ->
                                val animateProgress by animateFloatAsState(
                                    targetValue = if (selectedColor == color.background) 1f else 0f,
                                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                                )

                                LoadingIndicator(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .then(
                                            if (selectedColor == color.background) {
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = Color.White,
                                                    shape = MaterialShapes.Square.toShape()
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable(interactionSource = null, indication = null) {
                                            selectedColor = color.background
                                            onSelectedColor = color.onBackground
                                        },
                                    color = color.background,
                                    progress = { animateProgress },
                                    polygons = listOf(MaterialShapes.Circle, MaterialShapes.Square)
                                )

                            }
                        }
                    }
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
        context, "${context.packageName}.fileprovider", // Use your FileProvider authority
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun SD() {
    ToggleButton(
        checked = true,
        shapes = ToggleButtonDefaults.shapes(checkedShape = RoundedCornerShape(26.dp)),
        onCheckedChange = {
//                            showColorsDialog = it
        }) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.TextFields, contentDescription = "adjust text")
            Spacer(Modifier.width(5.dp))

            Text("Text")

        }
    }
}