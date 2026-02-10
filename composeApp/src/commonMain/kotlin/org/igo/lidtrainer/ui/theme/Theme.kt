package org.igo.lidtrainer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// ============================================================
// Кастомные темы (брендовые цвета из Color.kt)
// ============================================================

// Светлая тема
private val LightColors = lightColorScheme(
    // Primary
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,

    // Secondary
    secondary = LightSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondary = LightOnSecondary,
    onSecondaryContainer = LightOnSecondaryContainer,

    // Background
    background = LightBackground,
    onBackground = LightOnBackground,

    // Surface
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceContainer = LightSurfaceContainer,
    onSurfaceVariant = LightOnSurfaceVariant,

    // Borders & Dividers
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,

    // Error
    error = ErrorColor
)

// Тёмная тема
private val DarkColors = darkColorScheme(
    // Primary
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,

    // Secondary
    secondary = DarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondary = DarkOnSecondary,
    onSecondaryContainer = DarkOnSecondaryContainer,

    // Background
    background = DarkBackground,
    onBackground = DarkOnBackground,

    // Surface
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceContainer = DarkSurfaceContainer,
    onSurfaceVariant = DarkOnSurfaceVariant,

    // Borders & Dividers
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,

    // Error
    error = ErrorColor
)

// ============================================================
// Extension properties для кастомных цветов
// ============================================================

val ColorScheme.myCardBorder: Color
    get() = when (this) {
        LightColors -> LightMyCardBorder
        DarkColors -> DarkMyCardBorder
        else -> LightMyCardBorder
    }

val ColorScheme.myBarDivider: Color
    get() = when (this) {
        LightColors -> LightMyBarDivider
        DarkColors -> DarkMyBarDivider
        else -> LightMyBarDivider
    }

@Composable
fun LiDTrainerTheme(
    themeConfig: AppThemeConfig = AppThemeConfig.SYSTEM,
    languageConfig: AppLanguageConfig = AppLanguageConfig.SYSTEM,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()

    val colors = when (themeConfig) {
        AppThemeConfig.SYSTEM -> if (isSystemDark) DarkColors else LightColors
        AppThemeConfig.LIGHT -> LightColors
        AppThemeConfig.DARK -> DarkColors
    }

    val appStrings = rememberAppStrings(languageConfig)

    CompositionLocalProvider(
        LocalAppStrings provides appStrings
    ) {
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}
