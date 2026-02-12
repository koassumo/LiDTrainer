package org.igo.lidtrainer

import androidx.compose.runtime.Composable
import org.igo.lidtrainer.ui.theme.AppLanguageConfig

@Composable
expect fun UpdateSystemBarsTheme(isDark: Boolean)

@Composable
expect fun UpdateAppLanguage(language: AppLanguageConfig, content: @Composable () -> Unit)
