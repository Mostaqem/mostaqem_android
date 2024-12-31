package com.mostaqem.screens.screenshot.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap

@Composable
fun Screenshotable(content: @Composable () -> Unit): () -> Bitmap {
    val context = LocalContext.current
    val composeView = remember { ComposeView(context = context) }
    fun captureBitmap(): Bitmap = composeView.drawToBitmap()
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    MaterialTheme {
                        Surface() {
                            content()
                        }

                    }

                }
            }
        }, modifier = Modifier.wrapContentSize(unbounded = true)
    )

    return ::captureBitmap
}