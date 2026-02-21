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
    surfaceVariant = LightSurfaceVariant,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerLow = LightSurfaceContainerLow,
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
    surfaceVariant = DarkSurfaceVariant,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerLow = DarkSurfaceContainerLow,
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

// === Карточка ответа (Answer Card) ===

val ColorScheme.answerCardBorder: Color
    get() = if (this == LightColors) LightAnswerCardBorder else DarkAnswerCardBorder

val ColorScheme.answerCardBackground: Color
    get() = if (this == LightColors) LightAnswerCardBackground else DarkAnswerCardBackground

val ColorScheme.answerCardText: Color
    get() = if (this == LightColors) LightAnswerCardText else DarkAnswerCardText

val ColorScheme.correctAnswerBackground: Color
    get() = if (this == LightColors) LightCorrectAnswerBackground else DarkCorrectAnswerBackground

val ColorScheme.correctAnswerText: Color
    get() = if (this == LightColors) LightCorrectAnswerText else DarkCorrectAnswerText

val ColorScheme.incorrectAnswerBackground: Color
    get() = if (this == LightColors) LightIncorrectAnswerBackground else DarkIncorrectAnswerBackground

val ColorScheme.incorrectAnswerText: Color
    get() = if (this == LightColors) LightIncorrectAnswerText else DarkIncorrectAnswerText

val ColorScheme.answerStripDefault: Color
    get() = if (this == LightColors) LightAnswerStripDefault else DarkAnswerStripDefault

val ColorScheme.correctAnswerStrip: Color
    get() = if (this == LightColors) LightCorrectAnswerStrip else DarkCorrectAnswerStrip

val ColorScheme.incorrectAnswerStrip: Color
    get() = if (this == LightColors) LightIncorrectAnswerStrip else DarkIncorrectAnswerStrip

// === Бейджи (нижний бар + шторка) ===

val ColorScheme.correctBadge: Color
    get() = if (this == LightColors) LightCorrectBadge else DarkCorrectBadge

val ColorScheme.incorrectBadge: Color
    get() = if (this == LightColors) LightIncorrectBadge else DarkIncorrectBadge

// === Круговая диаграмма ===

val ColorScheme.chartCorrect: Color
    get() = if (this == LightColors) LightChartCorrect else DarkChartCorrect

val ColorScheme.chartIncorrect: Color
    get() = if (this == LightColors) LightChartIncorrect else DarkChartIncorrect

val ColorScheme.chartNotAnswered: Color
    get() = if (this == LightColors) LightChartNotAnswered else DarkChartNotAnswered

// === Цвет текста перевода ===

val ColorScheme.translationText: Color
    get() = if (this == LightColors) LightTranslationText else DarkTranslationText

// === Звёздочка избранного ===

val ColorScheme.favoriteStar: Color
    get() = if (this == LightColors) LightFavoriteStar else DarkFavoriteStar

// === Кнопка-тоггл перевода ===

val ColorScheme.translationToggleOnBackground: Color
    get() = if (this == LightColors) LightTranslationToggleOnBackground else DarkTranslationToggleOnBackground

val ColorScheme.translationToggleOnText: Color
    get() = if (this == LightColors) LightTranslationToggleOnText else DarkTranslationToggleOnText

val ColorScheme.translationToggleOffBackground: Color
    get() = if (this == LightColors) LightTranslationToggleOffBackground else DarkTranslationToggleOffBackground

val ColorScheme.translationToggleOffText: Color
    get() = if (this == LightColors) LightTranslationToggleOffText else DarkTranslationToggleOffText

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
