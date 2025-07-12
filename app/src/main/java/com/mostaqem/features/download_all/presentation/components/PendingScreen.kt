package com.mostaqem.features.download_all.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable


fun PendingScreen(modifier: Modifier = Modifier) {
    Column {
        Text("Ready to listen offline?")
        ElevatedButton(onClick = {}) {
            Text("Download")

        }
    }

}

@Preview()
@Composable
private fun PendingScreenPreview() {
    MaterialTheme {
        PendingScreen()
    }
}