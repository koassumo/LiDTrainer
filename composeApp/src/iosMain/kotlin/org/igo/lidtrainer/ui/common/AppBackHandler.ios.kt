package org.igo.lidtrainer.ui.common

import androidx.compose.runtime.Composable

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS не имеет физической кнопки "Назад"
}
