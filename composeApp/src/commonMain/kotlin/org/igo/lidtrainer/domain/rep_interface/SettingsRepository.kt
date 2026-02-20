package org.igo.lidtrainer.domain.rep_interface

import kotlinx.coroutines.flow.StateFlow
import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import org.igo.lidtrainer.ui.theme.AppThemeConfig

interface SettingsRepository {
    val themeState: StateFlow<AppThemeConfig>
    fun setTheme(theme: AppThemeConfig)

    val languageState: StateFlow<AppLanguageConfig>
    fun setLanguage(language: AppLanguageConfig)

    val languageContentState: StateFlow<String>
    fun setLanguageContentCode(code: String)
    fun isLanguageContentSelected(): Boolean

    val bundeslandState: StateFlow<String>
    fun setBundesland(code: String)
    fun isBundeslandSelected(): Boolean

    val showCorrectImmediatelyState: StateFlow<Boolean>
    fun setShowCorrectImmediately(enabled: Boolean)

    fun hasSeenSwipeHint(): Boolean
    fun setSeenSwipeHint()

    fun getPackVersion(): Int
    fun setPackVersion(version: Int)
}
