package org.igo.lidtrainer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository
import org.igo.lidtrainer.ui.screen.main.MainScreen
import org.igo.lidtrainer.ui.theme.AppThemeConfig
import org.igo.lidtrainer.ui.theme.LiDTrainerTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val repository = koinInject<SettingsRepository>()
    val themeConfig by repository.themeState.collectAsState()
    val languageConfig by repository.languageState.collectAsState()

    val useDarkTheme = when (themeConfig) {
        AppThemeConfig.SYSTEM -> isSystemInDarkTheme()
        AppThemeConfig.LIGHT -> false
        AppThemeConfig.DARK -> true
    }

    // key() полностью пересоздаёт всё UI при смене языка
    key(languageConfig) {
        UpdateAppLanguage(languageConfig) {
            LiDTrainerTheme(themeConfig = themeConfig, languageConfig = languageConfig) {
                UpdateSystemBarsTheme(useDarkTheme)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}
