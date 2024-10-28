package com.mostaqem.screens.settings

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Text(text = "SD", modifier = Modifier.consumeWindowInsets(innerPadding))
    }
}