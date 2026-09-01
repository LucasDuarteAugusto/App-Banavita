package br.com.banavita.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BanavitaGreen,
    onPrimary = Color.White,
    primaryContainer = BanavitaGreenLight,
    onPrimaryContainer = BanavitaGreenDark,
    secondary = BanavitaGreenDark,
    background = BanavitaSurface,
    surface = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = BanavitaGreenLight,
    onPrimary = BanavitaGreenDark,
    primaryContainer = BanavitaGreenDark,
    onPrimaryContainer = BanavitaGreenLight,
    secondary = BanavitaGreenLight,
)

@Composable
fun BanavitaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
