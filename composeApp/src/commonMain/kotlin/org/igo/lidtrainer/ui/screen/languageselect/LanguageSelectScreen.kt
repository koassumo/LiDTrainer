package org.igo.lidtrainer.ui.screen.languageselect

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.getSystemLanguageCode
import org.igo.lidtrainer.ui.common.CommonButton
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun LanguageSelectScreen(
    onLanguageSelected: (String) -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    topBar.title = strings.languageSelectTitle
    topBar.canNavigateBack = false

    val systemLang = remember { getSystemLanguageCode() }

    val languages = listOf(
        "en" to strings.languageEn,
        "ru" to strings.languageRu,
        "de" to strings.languageDe
    )

    val defaultSelection = if (languages.any { it.first == systemLang }) {
        systemLang
    } else {
        "en"
    }

    var selectedLanguage by remember { mutableStateOf(defaultSelection) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ScreenPaddingSides)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        Text(
            text = strings.languageSelectPrompt,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = Dimens.SpaceMedium)
        )

        languages.forEach { (code, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedLanguage = code }
                    .padding(vertical = Dimens.SpaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (code == selectedLanguage),
                    onClick = null
                )

                Spacer(modifier = Modifier.width(Dimens.SpaceMedium))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        CommonButton(
            text = strings.languageSelectContinue,
            onClick = { onLanguageSelected(selectedLanguage) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.ScreenPaddingBottom))
    }
}
