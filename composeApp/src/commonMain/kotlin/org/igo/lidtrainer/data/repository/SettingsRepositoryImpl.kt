package org.igo.lidtrainer.data.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository
import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import org.igo.lidtrainer.ui.theme.AppThemeConfig

class SettingsRepositoryImpl(
    private val settings: Settings
) : SettingsRepository {

    private val _themeState = MutableStateFlow(getCurrentTheme())
    override val themeState: StateFlow<AppThemeConfig> = _themeState.asStateFlow()

    private val _languageState = MutableStateFlow(getCurrentLanguage())
    override val languageState: StateFlow<AppLanguageConfig> = _languageState.asStateFlow()

    override fun setTheme(theme: AppThemeConfig) {
        settings.putString(KEY_THEME, theme.name)
        _themeState.value = theme
    }

    override fun setLanguage(language: AppLanguageConfig) {
        settings.putString(KEY_LANGUAGE, language.name)
        _languageState.value = language
    }

    private fun getCurrentTheme(): AppThemeConfig {
        val savedName = settings.getString(KEY_THEME, AppThemeConfig.SYSTEM.name)
        return try {
            AppThemeConfig.valueOf(savedName)
        } catch (e: Exception) {
            AppThemeConfig.SYSTEM
        }
    }

    private fun getCurrentLanguage(): AppLanguageConfig {
        val savedName = settings.getString(KEY_LANGUAGE, AppLanguageConfig.SYSTEM.name)
        return try {
            AppLanguageConfig.valueOf(savedName)
        } catch (e: Exception) {
            AppLanguageConfig.SYSTEM
        }
    }

    private val _languageContentState = MutableStateFlow(getCurrentLanguageContent())
    override val languageContentState: StateFlow<String> = _languageContentState.asStateFlow()

    override fun setLanguageContentCode(code: String) {
        settings.putString(KEY_LANGUAGE_CONTENT, code)
        _languageContentState.value = code
    }

    override fun isLanguageContentSelected(): Boolean {
        return _languageContentState.value.isNotEmpty()
    }

    private fun getCurrentLanguageContent(): String {
        return settings.getString(KEY_LANGUAGE_CONTENT, "")
    }

    private val _bundeslandState = MutableStateFlow(getCurrentBundesland())
    override val bundeslandState: StateFlow<String> = _bundeslandState.asStateFlow()

    override fun setBundesland(code: String) {
        settings.putString(KEY_BUNDESLAND, code)
        _bundeslandState.value = code
    }

    override fun isBundeslandSelected(): Boolean {
        return _bundeslandState.value.isNotEmpty()
    }

    private fun getCurrentBundesland(): String {
        return settings.getString(KEY_BUNDESLAND, "")
    }

    private val _showCorrectImmediatelyState = MutableStateFlow(getCurrentShowCorrectImmediately())
    override val showCorrectImmediatelyState: StateFlow<Boolean> = _showCorrectImmediatelyState.asStateFlow()

    override fun setShowCorrectImmediately(enabled: Boolean) {
        settings.putBoolean(KEY_SHOW_CORRECT_IMMEDIATELY, enabled)
        _showCorrectImmediatelyState.value = enabled
    }

    private fun getCurrentShowCorrectImmediately(): Boolean {
        return settings.getBoolean(KEY_SHOW_CORRECT_IMMEDIATELY, false)
    }

    override fun hasSeenSwipeHint(): Boolean {
        return settings.getBoolean(KEY_SWIPE_HINT_SEEN, false)
    }

    override fun setSeenSwipeHint() {
        settings.putBoolean(KEY_SWIPE_HINT_SEEN, true)
    }

    companion object {
        private const val KEY_THEME = "app_theme_key"
        private const val KEY_LANGUAGE = "app_language_key"
        private const val KEY_LANGUAGE_CONTENT = "language_content_code"
        private const val KEY_BUNDESLAND = "bundesland_key"
        private const val KEY_SHOW_CORRECT_IMMEDIATELY = "show_correct_immediately_key"
        private const val KEY_SWIPE_HINT_SEEN = "swipe_hint_seen_key"
    }
}
