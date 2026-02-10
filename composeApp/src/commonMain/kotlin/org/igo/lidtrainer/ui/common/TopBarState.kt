package org.igo.lidtrainer.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

class TopBarState {
    var title by mutableStateOf("")
    var canNavigateBack by mutableStateOf(false)
    var onNavigateBack: () -> Unit = {}
}

val LocalTopBarState = staticCompositionLocalOf<TopBarState> {
    error("TopBarState not provided")
}
