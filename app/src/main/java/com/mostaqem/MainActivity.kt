package com.mostaqem

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.datastore.dataStore
import com.google.common.io.Resources
import com.mostaqem.core.ui.app.MostaqemApp
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.features.language.domain.LanguageManager
import com.mostaqem.features.settings.domain.AppSettingsSerializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale


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

    override fun attachBaseContext(newBase: Context?) {
        val languageTag = getSavedLanguage(newBase!!)
        val contextWithLocale = getContextForLanguage(newBase, languageTag)
        super.attachBaseContext(contextWithLocale)
    }

}

fun getSavedLanguage(context: Context): String {
    return runBlocking {
        LanguageManager(context).getLanguageCode()

    }
}

fun getContextForLanguage(context: Context, languageTag: String): Context {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return context
    if (languageTag.isEmpty()) return context

    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)

    val configuration = context.resources.configuration
    configuration.setLocales(LocaleList(locale))

    return context.createConfigurationContext(configuration)
}

