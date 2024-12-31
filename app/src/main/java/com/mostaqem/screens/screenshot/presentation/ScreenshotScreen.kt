package com.mostaqem.screens.screenshot.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.mostaqem.R
import com.mostaqem.core.ui.theme.kufamFontFamily
import com.mostaqem.screens.player.data.PlayerSurah
import com.mostaqem.screens.screenshot.domain.toArabicNumbers
import com.mostaqem.screens.screenshot.presentation.components.Screenshotable
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotScreen(
    message: String,
    surahName: String,
    verseNumber: Int,
    navController: NavController,
    playerSurah: MutableState<PlayerSurah>
) {
    val context = LocalContext.current
    val view = LocalView.current

    CenterAlignedTopAppBar(title = { Text(text = "مشاركة آية") }, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "back")
        }
    })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()

    ) {
        Spacer(modifier = Modifier.height(50.dp))

        val screenshot = Screenshotable {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(360.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.surah_vector),
                        modifier = Modifier.width(350.dp),
                        contentDescription = "verse",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),

                        )
                    Text(text = "سورة ${surahName}", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.basmallah),
                    contentDescription = "basmallah",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .width(220.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(30.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.verse_icon),
                        contentDescription = "verse",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        alignment = Alignment.Center,
                        modifier = Modifier.size(60.dp)

                    )
                    Text(
                        text = verseNumber.toString().toArabicNumbers(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "مستقيم",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = kufamFontFamily)
                )


            }


        }
        Spacer(modifier = Modifier.height(50.dp))

        TextButton(onClick = {
            val bitmap = screenshot.invoke()
            val uri = saveBitmapToFile(context, bitmap)
            shareToInstagramStory(context, uri)

        }) {
            Text(text = "Share")
        }


    }
}

private fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri {
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "shared_image.png"
    )
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    return FileProvider.getUriForFile(
        context, "${context.packageName}.provider", file
    )
}

private fun shareToInstagramStory(context: Context, imageUri: Uri) {
    val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        setDataAndType(imageUri, "image/png")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    if (context.packageManager.resolveActivity(intent, 0) != null) {
        context.startActivity(intent)
    } else {
        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "Share Image"))
    }
}

