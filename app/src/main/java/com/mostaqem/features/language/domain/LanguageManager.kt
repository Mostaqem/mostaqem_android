package com.mostaqem.features.language.domain

import android.app.Activity
import android.app.LocaleConfig
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.mostaqem.MainActivity
import com.mostaqem.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


enum class AppLanguages(val displayLanguage: String, val rtl: Boolean = false, val code: String) {
    SYSTEM(displayLanguage = "System", code = "system"),
    ENGLISH(displayLanguage = "English", code = "en"),
    ARABIC(
        displayLanguage = "العربية",
        code = "ar",
        rtl = true
    ),
}


@Singleton
class LanguageManager @Inject constructor(val context: Context) {

    fun changeLanguage(languageCode: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                val localeManager = context
                    .getSystemService(LocaleManager::class.java)
                localeManager.overrideLocaleConfig = LocaleConfig(
                    LocaleList.forLanguageTags(languageCode)
                )
                restartApp(context)

            } catch (e: NullPointerException) {
                restartApp(context)
            }
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(
                    languageCode
                )
            )
            restartApp(context)

        }

    }


    fun getLanguageCode(): String? {
        val locale: Locale? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager?.overrideLocaleConfig
                ?.supportedLocales
                ?.get(0)
        } else {
            AppCompatDelegate.getApplicationLocales().get(0)
        }

        Log.d("Locale", "getLanguageCode: ${locale}")

        return locale?.language ?: "system"
    }


    private fun getDefaultLanguageCode(): String {
        return runBlocking {
            context.dataStore.data.first().language.code
        }
    }

    fun restartApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }

        context.startActivity(intent)

        // Finish current activity if it's an Activity context
        if (context is Activity) {
            context.finish()
            // Optional: Add animation override
        }


    }


}