package org.igo.lidtrainer.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.ui.common.AppBackHandler
import org.igo.lidtrainer.ui.common.CommonTopBar
import org.igo.lidtrainer.ui.common.ExitDialog
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.common.TopBarState
import org.igo.lidtrainer.ui.common.exitApp
import org.igo.lidtrainer.ui.navigation.Destinations
import org.igo.lidtrainer.ui.screen.dashboard.DashboardScreen
import org.igo.lidtrainer.ui.screen.bundeslandselect.BundeslandSelectScreen
import org.igo.lidtrainer.ui.screen.languageselect.LanguageSelectScreen
import org.igo.lidtrainer.ui.screen.lesson.LessonScreen
import org.igo.lidtrainer.ui.screen.lesson.LessonViewModel
import org.igo.lidtrainer.ui.screen.settings.SettingsScreen
import org.igo.lidtrainer.ui.screen.settings.SettingsViewModel
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
                    backButtonDescription = strings.backButtonTooltip,
                    actions = {
                        if (currentRoute != Destinations.SETTINGS && currentRoute != Destinations.LANGUAGE_SELECT && currentRoute != Destinations.BUNDESLAND_SELECT) {
                            IconButton(
                                onClick = { viewModel.navigateTo(Destinations.SETTINGS) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = strings.settingsTitle
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    Destinations.LANGUAGE_SELECT -> {
                        LanguageSelectScreen(
                            onLanguageSelected = { code ->
                                viewModel.onLanguageSelected(code)
                            }
                        )
                    }
                    Destinations.BUNDESLAND_SELECT -> {
                        BundeslandSelectScreen(
                            onBundeslandSelected = { code ->
                                viewModel.onBundeslandSelected(code)
                            },
                            onNavigateBack = { viewModel.navigateBack() }
                        )
                    }
                    Destinations.DASHBOARD -> {
                        DashboardScreen(
                            totalQuestions = totalQuestions,
                            onAllQuestionsClick = {
                                viewModel.navigateTo(Destinations.LESSON)
                            }
                        )
                    }
                    Destinations.LESSON -> {
                        val lessonViewModel = koinViewModel<LessonViewModel>()
                        LessonScreen(
                            viewModel = lessonViewModel,
                            onNavigateBack = { viewModel.navigateBack() }
                        )
                    }
                    Destinations.SETTINGS -> {
                        val settingsViewModel = koinViewModel<SettingsViewModel>()
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { viewModel.navigateBack() }
                        )
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
