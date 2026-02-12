package org.igo.lidtrainer

import androidx.compose.runtime.Composable
import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import platform.Foundation.NSUserDefaults

@Composable
actual fun UpdateSystemBarsTheme(isDark: Boolean) {
    // iOS управляет статус-баром автоматически через Info.plist
}

private val originalAppleLanguages: List<*>? =
    NSUserDefaults.standardUserDefaults.arrayForKey("AppleLanguages")

@Composable
actual fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit) {
    val languageCode = when (language) {
        AppLanguageConfig.SYSTEM -> null
        AppLanguageConfig.EN -> "en"
        AppLanguageConfig.RU -> "ru"
        AppLanguageConfig.DE -> "de"
    }

    if (languageCode != null) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(languageCode), forKey = "AppleLanguages")
    } else {
        if (originalAppleLanguages != null) {
            NSUserDefaults.standardUserDefaults.setObject(originalAppleLanguages, forKey = "AppleLanguages")
        } else {
            NSUserDefaults.standardUserDefaults.removeObjectForKey("AppleLanguages")
        }
    }
    NSUserDefaults.standardUserDefaults.synchronize()

    content()
}
