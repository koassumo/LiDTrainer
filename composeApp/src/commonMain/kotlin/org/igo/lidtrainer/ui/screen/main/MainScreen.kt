package org.igo.lidtrainer.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.ui.common.AppBackHandler
import org.igo.lidtrainer.ui.common.CommonTopBar
import org.igo.lidtrainer.ui.common.ExitDialog
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.common.TopBarState
import org.igo.lidtrainer.ui.common.exitApp
import org.igo.lidtrainer.ui.navigation.Destinations
import org.igo.lidtrainer.ui.screen.dashboard.DashboardScreen
import org.igo.lidtrainer.ui.theme.LocalAppStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel = koinViewModel<MainViewModel>()
    val strings = LocalAppStrings.current

    val currentRoute by viewModel.currentRoute.collectAsState()
    val totalQuestions by viewModel.totalQuestions.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarState = remember { TopBarState() }

    AppBackHandler(enabled = true) {
        if (currentRoute == Destinations.DASHBOARD) {
            showExitDialog = true
        } else {
            viewModel.navigateBack()
        }
    }

    CompositionLocalProvider(LocalTopBarState provides topBarState) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CommonTopBar(
                    title = topBarState.title,
                    canNavigateBack = topBarState.canNavigateBack,
                    navigateUp = topBarState.onNavigateBack,
                    backButtonDescription = strings.backButtonTooltip
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    Destinations.DASHBOARD -> {
                        DashboardScreen(
                            totalQuestions = totalQuestions,
                            onAllQuestionsClick = {
                                viewModel.navigateTo(Destinations.LEARN)
                            }
                        )
                    }
                    Destinations.LEARN -> {
                        topBarState.title = strings.learnTitle
                        topBarState.canNavigateBack = true
                        topBarState.onNavigateBack = { viewModel.navigateBack() }
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Learn — coming soon")
                        }
                    }
                    Destinations.SETTINGS -> {
                        topBarState.title = strings.settingsTitle
                        topBarState.canNavigateBack = true
                        topBarState.onNavigateBack = { viewModel.navigateBack() }
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Settings — coming soon")
                        }
                    }
                }
            }
        }
    }

    ExitDialog(
        showDialog = showExitDialog,
        onDismiss = { showExitDialog = false },
        onConfirmExit = { exitApp() }
    )
}
