package com.mostaqem.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mostaqem.dataStore
import com.mostaqem.features.settings.data.AppSettings
import kotlinx.coroutines.flow.map

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    primaryContainer = Pink80

)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MostaqemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val context = LocalContext.current
    val languageCode = context.dataStore.data.map { it.language.code }
        .collectAsStateWithLifecycle(initialValue = "en")

    val adaptiveFontFamily = if (languageCode.value == "ar") {
        kufamFontFamily
    } else {
        productFontFamily
    }

    val typography = androidx.compose.material3.Typography(
        titleLarge = TextStyle(
            fontFamily = adaptiveFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.W500
        ),
        labelLarge = TextStyle(
            fontFamily = adaptiveFontFamily,
        ),
        headlineLarge = TextStyle(
            fontFamily = adaptiveFontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold
        )
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}