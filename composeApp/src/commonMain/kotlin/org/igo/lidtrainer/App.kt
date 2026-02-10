package org.igo.lidtrainer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.ui.screen.main.MainScreen
import org.igo.lidtrainer.ui.theme.LiDTrainerTheme

@Composable
fun App() {
    LiDTrainerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}
