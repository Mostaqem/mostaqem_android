package com.mostaqem

import android.app.ComponentCaller
import android.app.LocaleManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import com.mostaqem.core.ui.app.MostaqemApp
import com.mostaqem.core.ui.theme.MostaqemTheme
import com.mostaqem.features.language.domain.LanguageManager
import com.mostaqem.features.notifications.domain.NotificationService
import com.mostaqem.features.player.presentation.PlayerViewModel
import com.mostaqem.features.settings.domain.AppSettingsSerializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlin.system.exitProcess


val Context.dataStore by dataStore(
    fileName = "app-settings.json", serializer = AppSettingsSerializer
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("Error", e.stackTraceToString())
            startCrashReportActivity(applicationContext, e)
        }
        enableEdgeToEdge()
        setContent {
            MostaqemTheme {
                MostaqemApp()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val languageTag = getSavedLanguage(newBase)
        val contextWithLocale = getContextForLanguage(newBase, languageTag)
        super.attachBaseContext(contextWithLocale)
    }

}


private fun startCrashReportActivity(context: Context, th: Throwable) {
    th.printStackTrace()
    val intent = Intent(context, CrashReportActivity::class.java).apply {
        putExtra("error", Log.getStackTraceString(th))
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    context.startActivity(intent)
    android.os.Process.killProcess(android.os.Process.myPid())
    exitProcess(10)

}

fun getSavedLanguage(context: Context): String? {
    return LanguageManager(context).getLanguageCode()
}

fun getContextForLanguage(context: Context, languageTag: String?): Context {
    if (languageTag.isNullOrEmpty()) {
        // Use system locale (no override)
        return context
    }
    if (languageTag == "system") return context


    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)

    val configuration = context.resources.configuration
    configuration.setLocales(LocaleList(locale))

    return context.createConfigurationContext(configuration)
}