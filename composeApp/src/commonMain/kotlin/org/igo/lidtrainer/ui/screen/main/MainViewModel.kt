package org.igo.lidtrainer.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository
import org.igo.lidtrainer.domain.usecase.LoadNotesFromJsonUseCase
import org.igo.lidtrainer.ui.navigation.Destinations
import org.igo.lidtrainer.ui.theme.AppLanguageConfig

class MainViewModel(
    private val loadNotesFromJsonUseCase: LoadNotesFromJsonUseCase,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _currentRoute = MutableStateFlow("")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0L)
    val totalQuestions: StateFlow<Long> = _totalQuestions.asStateFlow()

    // Стек навигации для правильной обработки "Назад"
    private val navigationStack = mutableListOf<String>()

    init {
        determineInitialRoute()
    }

    private fun determineInitialRoute() {
        viewModelScope.launch {
            val route = when {
                !settingsRepository.isLanguageContentSelected() -> Destinations.LANGUAGE_SELECT
                !settingsRepository.isBundeslandSelected() -> Destinations.BUNDESLAND_SELECT
                else -> {
                    loadInitialData()
                    Destinations.DASHBOARD
                }
            }
            _currentRoute.value = route
            navigationStack.add(route)
        }
    }

    private suspend fun loadInitialData() {
        try {
            val languageCode = settingsRepository.languageContentState.value
            loadNotesFromJsonUseCase(languageCode)
        } catch (e: Exception) {
            // Данные уже загружены или ошибка — продолжаем
        }
        updateStatistics()
    }

    private suspend fun updateStatistics() {
        val bundesland = settingsRepository.bundeslandState.value
        val stats = if (bundesland.isNotEmpty()) {
            noteRepository.getStatisticsByBundesland(bundesland)
        } else {
            noteRepository.getStatistics()
        }
        _totalQuestions.value = stats.totalCount
    }

    fun onLanguageSelected(code: String) {
        viewModelScope.launch {
            settingsRepository.setLanguageContentCode(code)
            val uiLanguage = when (code) {
                "en" -> AppLanguageConfig.EN
                "ru" -> AppLanguageConfig.RU
                "de" -> AppLanguageConfig.DE
                else -> AppLanguageConfig.EN
            }
            settingsRepository.setLanguage(uiLanguage)
            _currentRoute.value = Destinations.BUNDESLAND_SELECT
            navigationStack.clear()
            navigationStack.add(Destinations.LANGUAGE_SELECT)
            navigationStack.add(Destinations.BUNDESLAND_SELECT)
        }
    }

    fun onBundeslandSelected(code: String) {
        viewModelScope.launch {
            settingsRepository.setBundesland(code)
            loadInitialData()
            navigateTo(Destinations.DASHBOARD)
        }
    }

    fun navigateTo(route: String) {
        val mainTabs = listOf(
            Destinations.DASHBOARD
        )

        if (route in mainTabs) {
            navigationStack.clear()
            navigationStack.add(Destinations.DASHBOARD)
        } else {
            if (_currentRoute.value != route) {
                navigationStack.add(_currentRoute.value)
            }
        }

        _currentRoute.value = route
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            _currentRoute.value = navigationStack.removeLast()
        } else {
            _currentRoute.value = Destinations.DASHBOARD
        }
    }
}
