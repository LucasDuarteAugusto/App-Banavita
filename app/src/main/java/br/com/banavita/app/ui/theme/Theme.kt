package br.com.banavita.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    background = BanavitaDarkBackground,
    surface = BanavitaDarkSurface,
)

@Composable
fun BanavitaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
