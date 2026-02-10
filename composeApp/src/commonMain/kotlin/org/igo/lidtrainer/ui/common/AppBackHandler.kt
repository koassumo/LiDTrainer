package org.igo.lidtrainer.ui.common

import androidx.compose.runtime.Composable

/**
 * Кроссплатформенная обёртка над обработчиком кнопки "Назад".
 * Android: делегирует к androidx.activity.compose.BackHandler
 * iOS: пустая реализация (нет физической кнопки "Назад")
 */
@Composable
expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit)
