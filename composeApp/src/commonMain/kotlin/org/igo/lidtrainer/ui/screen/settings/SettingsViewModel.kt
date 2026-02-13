package org.igo.lidtrainer.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository
import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import org.igo.lidtrainer.ui.theme.AppThemeConfig

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.themeState.collect { newTheme ->
                _state.update { it.copy(selectedTheme = newTheme) }
            }
        }

        viewModelScope.launch {
            repository.languageState.collect { newLanguage ->
                _state.update { it.copy(selectedLanguage = newLanguage) }
            }
        }

        viewModelScope.launch {
            repository.languageContentState.collect { newCode ->
                _state.update { it.copy(selectedLanguageContent = newCode) }
            }
        }

        viewModelScope.launch {
            repository.bundeslandState.collect { newBundesland ->
                _state.update { it.copy(selectedBundesland = newBundesland) }
            }
        }

        viewModelScope.launch {
            repository.showCorrectImmediatelyState.collect { enabled ->
                _state.update { it.copy(showCorrectImmediately = enabled) }
            }
        }
    }

    fun updateTheme(newTheme: AppThemeConfig) {
        repository.setTheme(newTheme)
    }

    fun updateLanguage(newLanguage: AppLanguageConfig) {
        repository.setLanguage(newLanguage)
    }

    fun updateLanguageContent(newCode: String) {
        repository.setLanguageContentCode(newCode)
    }

    fun updateBundesland(code: String) {
        repository.setBundesland(code)
    }

    fun updateShowCorrectImmediately(enabled: Boolean) {
        repository.setShowCorrectImmediately(enabled)
    }
}
