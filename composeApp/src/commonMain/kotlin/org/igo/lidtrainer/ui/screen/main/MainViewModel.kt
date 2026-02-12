package org.igo.lidtrainer.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.usecase.LoadNotesFromJsonUseCase
import org.igo.lidtrainer.ui.navigation.Destinations

class MainViewModel(
    private val loadNotesFromJsonUseCase: LoadNotesFromJsonUseCase,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _currentRoute = MutableStateFlow(Destinations.DASHBOARD)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _totalQuestions = MutableStateFlow(0L)
    val totalQuestions: StateFlow<Long> = _totalQuestions.asStateFlow()

    // Стек навигации для правильной обработки "Назад"
    private val navigationStack = mutableListOf(Destinations.DASHBOARD)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Пока хардкодим "ru" как родной язык (потом будет из Settings)
                loadNotesFromJsonUseCase("ru")
            } catch (e: Exception) {
                // Данные уже загружены или ошибка — продолжаем
            }
            updateStatistics()
        }
    }

    private suspend fun updateStatistics() {
        val stats = noteRepository.getStatistics()
        _totalQuestions.value = stats.totalCount
    }

    fun navigateTo(route: String) {
        val mainTabs = listOf(
            Destinations.DASHBOARD,
            Destinations.SETTINGS
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
