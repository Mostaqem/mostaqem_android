package com.mostaqem

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.dataStore
import com.mostaqem.core.ui.app.MostaqemApp
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.screens.settings.domain.AppSettingsSerializer
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore by dataStore(
    fileName = "app-settings.json", serializer = AppSettingsSerializer
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MostaqemTheme {
                MostaqemApp()
            }
        }
    }
}
