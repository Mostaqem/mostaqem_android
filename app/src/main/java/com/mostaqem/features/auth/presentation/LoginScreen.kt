package com.mostaqem.features.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Scaffold {
        Column(modifier = Modifier.padding(paddingValues = it)) {
            TextButton(onClick = {}) {
                Text("Login with Google")
            }

        }
    }
}
