package com.confecciones.esperanza.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    secondary = BlueAccent,
    tertiary = PinkAccent,
    background = AppBackground,
    surface = AppSurface,
    surfaceVariant = AppSurfaceAlt,
    outline = AppOutline,
    onPrimary = AppSurface,
    onSecondary = AppSurface,
    onTertiary = AppSurface,
    onBackground = AppTextPrimary,
    onSurface = AppTextPrimary,
    error = AppError,
    onError = AppSurface
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    secondary = BlueAccent,
    tertiary = PinkAccent,
    background = AppBackground,
    surface = AppSurface,
    surfaceVariant = AppSurfaceAlt,
    outline = AppOutline,
    onPrimary = AppSurface,
    onSecondary = AppSurface,
    onTertiary = AppSurface,
    onBackground = AppTextPrimary,
    onSurface = AppTextPrimary,
    error = AppError,
    onError = AppSurface
)

@Composable
fun ConfeccionesEsperanzaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
