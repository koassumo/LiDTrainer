package org.igo.lidtrainer.ui.screen.languageselect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun LanguageSelectScreen(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    title: String? = null,
    canNavigateBack: Boolean = false,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    if (title != "") {
        val topBar = LocalTopBarState.current
        topBar.title = title ?: strings.languageSelectTitle
        topBar.canNavigateBack = canNavigateBack
        topBar.onNavigateBack = onNavigateBack ?: {}
    }

    val languages = listOf(
        "en" to strings.languageEn,
        "ru" to strings.languageRu,
        "de" to strings.languageDe,
        "es" to strings.languageEs
    )

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingSides)
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            languages.forEach { (code, label) ->
                ListItem(
                    modifier = Modifier.clickable { onLanguageSelected(code) },
                    headlineContent = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingContent = {
                        RadioButton(
                            selected = (code == selectedLanguage),
                            onClick = null
                        )
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
