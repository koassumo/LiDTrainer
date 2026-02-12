package org.igo.lidtrainer.ui.screen.settings

import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import org.igo.lidtrainer.ui.theme.AppThemeConfig

data class SettingsState(
    val selectedTheme: AppThemeConfig = AppThemeConfig.SYSTEM,
    val selectedLanguage: AppLanguageConfig = AppLanguageConfig.SYSTEM,
    val selectedLanguageContent: String = ""
)
