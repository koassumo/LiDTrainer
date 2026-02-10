package org.igo.lidtrainer.ui.screen.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.igo.lidtrainer.ui.navigation.Destinations

class MainViewModel : ViewModel() {

    private val _currentRoute = MutableStateFlow(Destinations.DASHBOARD)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // Стек навигации для правильной обработки "Назад"
    private val navigationStack = mutableListOf(Destinations.DASHBOARD)

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
