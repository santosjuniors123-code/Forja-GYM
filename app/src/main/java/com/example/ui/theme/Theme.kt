package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ForgeOrange,
    onPrimary = Color.White,
    primaryContainer = ForgeOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = ForgeOrangeLight,
    onSecondary = Color.Black,
    secondaryContainer = ForgeCardElevated,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = ForgeGreen,
    onTertiary = Color.White,
    background = ForgeBlack,
    onBackground = TextPrimaryDark,
    surface = ForgeDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = ForgeCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = ForgeBorder,
    outlineVariant = ForgeBorderLight
)

private val LightColorScheme = lightColorScheme(
    primary = ForgeOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC8),
    onPrimaryContainer = Color(0xFF3B1200),
    secondary = ForgeOrangeDark,
    onSecondary = Color.White,
    background = ForgeLightBg,
    onBackground = TextPrimaryLight,
    surface = ForgeLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F1F4),
    onSurfaceVariant = TextSecondaryLight,
    outline = ForgeLightBorder
)

@Composable
fun ForjaGymTheme(
    darkTheme: Boolean = true, // Default to Dark commercial gym aesthetic
    dynamicColor: Boolean = false, // Keep branded ForjaGym orange identity
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
