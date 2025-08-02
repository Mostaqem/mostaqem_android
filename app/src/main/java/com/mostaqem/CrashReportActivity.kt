package com.mostaqem

import android.content.ClipData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mostaqem.core.ui.theme.MostaqemTheme
import kotlinx.coroutines.launch


class CrashReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val errorMessage: String = intent.getStringExtra("error").toString()
        setContent {
            MostaqemTheme {
                val clipboardManager: Clipboard = LocalClipboard.current
                val scope = rememberCoroutineScope()
                CrashReportPage(errorMessage) {
                    val clipData = ClipData.newPlainText("error", errorMessage)
                    val clipEntry = ClipEntry(clipData)
                    scope.launch {
                        clipboardManager.setClipEntry(clipEntry)
                    }
                    this.finishAffinity()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) finishAffinity()
    }
}

@Composable
@Preview
fun CrashReportPage(errorMessage: String = "ERROR_EXAMPLE", onClick: () -> Unit = {}) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            HorizontalDivider()
            TextButton(onClick, modifier = Modifier.fillMaxWidth()) {
                Text("Copy and Exit")

            }
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 60.dp,
                    bottom = 12.dp
                ),
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            )
        }
    }
}