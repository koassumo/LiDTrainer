package org.igo.lidtrainer.ui.screen.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.domain.model.Bundesland
import org.igo.lidtrainer.ui.common.AppBackHandler
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.AppLanguageConfig
import org.igo.lidtrainer.ui.theme.AppThemeConfig
import org.igo.lidtrainer.ui.theme.LocalAppStrings

private sealed interface SettingsPage {
    data object MainList : SettingsPage
    data object ThemeSelection : SettingsPage
    data object LanguageSelection : SettingsPage
    data object LanguageContentSelection : SettingsPage
    data object BundeslandSelection : SettingsPage
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val state by viewModel.state.collectAsState()
    val strings = LocalAppStrings.current

    var currentPage by remember { mutableStateOf<SettingsPage>(SettingsPage.MainList) }

    AppBackHandler(enabled = currentPage != SettingsPage.MainList) {
        currentPage = SettingsPage.MainList
    }

    when (currentPage) {
        SettingsPage.MainList -> {
            topBar.title = strings.settingsTitle
            topBar.canNavigateBack = true
            topBar.onNavigateBack = onNavigateBack
        }
        SettingsPage.ThemeSelection -> {
            topBar.title = strings.themeSection
            topBar.canNavigateBack = true
            topBar.onNavigateBack = { currentPage = SettingsPage.MainList }
        }
        SettingsPage.LanguageSelection -> {
            topBar.title = strings.languageSection
            topBar.canNavigateBack = true
            topBar.onNavigateBack = { currentPage = SettingsPage.MainList }
        }
        SettingsPage.LanguageContentSelection -> {
            topBar.title = strings.languageContentSection
            topBar.canNavigateBack = true
            topBar.onNavigateBack = { currentPage = SettingsPage.MainList }
        }
        SettingsPage.BundeslandSelection -> {
            topBar.title = strings.bundeslandSection
            topBar.canNavigateBack = true
            topBar.onNavigateBack = { currentPage = SettingsPage.MainList }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState != SettingsPage.MainList) {
                    slideInHorizontally(
                        initialOffsetX = { width -> width },
                        animationSpec = tween(300)
                    ) + fadeIn() togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { width -> -width / 2 },
                                animationSpec = tween(300)
                            ) + fadeOut()
                } else {
                    slideInHorizontally(
                        initialOffsetX = { width -> -width / 2 },
                        animationSpec = tween(300)
                    ) + fadeIn() togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { width -> width },
                                animationSpec = tween(300)
                            ) + fadeOut()
                }
            },
            label = "settings_page_transition"
        ) { page ->
            when (page) {
                SettingsPage.MainList -> {
                    SettingsMainList(
                        currentTheme = state.selectedTheme,
                        currentLanguage = state.selectedLanguage,
                        currentLanguageContent = state.selectedLanguageContent,
                        currentBundesland = state.selectedBundesland,
                        onThemeClick = { currentPage = SettingsPage.ThemeSelection },
                        onLanguageClick = { currentPage = SettingsPage.LanguageSelection },
                        onLanguageContentClick = { currentPage = SettingsPage.LanguageContentSelection },
                        onBundeslandClick = { currentPage = SettingsPage.BundeslandSelection }
                    )
                }

                SettingsPage.ThemeSelection -> {
                    SelectionScreen(
                        options = listOf(
                            AppThemeConfig.SYSTEM to strings.systemTheme,
                            AppThemeConfig.LIGHT to strings.lightTheme,
                            AppThemeConfig.DARK to strings.darkTheme
                        ),
                        selectedOption = state.selectedTheme,
                        onOptionSelected = { newTheme ->
                            viewModel.updateTheme(newTheme)
                            currentPage = SettingsPage.MainList
                        }
                    )
                }

                SettingsPage.LanguageSelection -> {
                    SelectionScreen(
                        options = listOf(
                            AppLanguageConfig.SYSTEM to strings.systemTheme,
                            AppLanguageConfig.EN to strings.languageEn,
                            AppLanguageConfig.RU to strings.languageRu,
                            AppLanguageConfig.DE to strings.languageDe
                        ),
                        selectedOption = state.selectedLanguage,
                        onOptionSelected = { newLanguage ->
                            viewModel.updateLanguage(newLanguage)
                            currentPage = SettingsPage.MainList
                        }
                    )
                }

                SettingsPage.LanguageContentSelection -> {
                    SelectionScreen(
                        options = listOf(
                            "en" to strings.languageEn,
                            "ru" to strings.languageRu,
                            "de" to strings.languageDe
                        ),
                        selectedOption = state.selectedLanguageContent,
                        onOptionSelected = { newCode ->
                            viewModel.updateLanguageContent(newCode)
                            currentPage = SettingsPage.MainList
                        }
                    )
                }

                SettingsPage.BundeslandSelection -> {
                    SelectionScreen(
                        options = Bundesland.entries.map { it.name to it.displayName },
                        selectedOption = state.selectedBundesland,
                        onOptionSelected = { newCode ->
                            viewModel.updateBundesland(newCode)
                            currentPage = SettingsPage.MainList
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsMainList(
    currentTheme: AppThemeConfig,
    currentLanguage: AppLanguageConfig,
    currentLanguageContent: String,
    currentBundesland: String,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onLanguageContentClick: () -> Unit,
    onBundeslandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPaddingSides)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        SettingsMenuItem(
            title = strings.themeSection,
            currentValue = when (currentTheme) {
                AppThemeConfig.SYSTEM -> strings.systemTheme
                AppThemeConfig.LIGHT -> strings.lightTheme
                AppThemeConfig.DARK -> strings.darkTheme
            },
            onClick = onThemeClick
        )

        HorizontalDivider()

        SettingsMenuItem(
            title = strings.languageSection,
            currentValue = when (currentLanguage) {
                AppLanguageConfig.SYSTEM -> strings.systemTheme
                AppLanguageConfig.EN -> strings.languageEn
                AppLanguageConfig.RU -> strings.languageRu
                AppLanguageConfig.DE -> strings.languageDe
            },
            onClick = onLanguageClick
        )

        HorizontalDivider()

        SettingsMenuItem(
            title = strings.languageContentSection,
            currentValue = when (currentLanguageContent) {
                "en" -> strings.languageEn
                "ru" -> strings.languageRu
                "de" -> strings.languageDe
                else -> currentLanguageContent
            },
            onClick = onLanguageContentClick
        )

        HorizontalDivider()

        SettingsMenuItem(
            title = strings.bundeslandSection,
            currentValue = Bundesland.entries.find { it.name == currentBundesland }?.displayName ?: currentBundesland,
            onClick = onBundeslandClick
        )

        HorizontalDivider()
    }
}

@Composable
private fun SettingsMenuItem(
    title: String,
    currentValue: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun <T> SelectionScreen(
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPaddingSides)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(value) }
                    .padding(vertical = Dimens.SpaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (value == selectedOption),
                    onClick = null
                )

                Spacer(modifier = Modifier.width(Dimens.SpaceMedium))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
