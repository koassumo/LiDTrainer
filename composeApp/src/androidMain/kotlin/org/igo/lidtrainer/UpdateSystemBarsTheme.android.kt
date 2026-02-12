package org.igo.lidtrainer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import java.util.Locale

@Composable
actual fun UpdateSystemBarsTheme(isDark: Boolean) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
        val insetsController = WindowInsetsControllerCompat(window, view)

        insetsController.isAppearanceLightStatusBars = !isDark
        insetsController.isAppearanceLightNavigationBars = !isDark
    }
}

private val originalSystemLocale: Locale = Locale.getDefault()

@Composable
actual fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit) {
    val newLocale = when (language) {
        AppLanguageConfig.SYSTEM -> originalSystemLocale
        AppLanguageConfig.EN -> Locale.ENGLISH
        AppLanguageConfig.RU -> Locale("ru")
        AppLanguageConfig.DE -> Locale.GERMAN
    }
    Locale.setDefault(newLocale)

    content()
}
