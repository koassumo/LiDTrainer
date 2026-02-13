package org.igo.lidtrainer.ui.screen.bundeslandselect

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
import org.igo.lidtrainer.domain.model.Bundesland
import org.igo.lidtrainer.ui.common.CommonButton
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun BundeslandSelectScreen(
    onBundeslandSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    topBar.title = strings.bundeslandSelectTitle
    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack

    val bundeslaender = Bundesland.entries
    var selectedBundesland by remember { mutableStateOf(bundeslaender.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ScreenPaddingSides)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        Text(
            text = strings.bundeslandSelectPrompt,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = Dimens.SpaceMedium)
        )

        bundeslaender.forEach { bundesland ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedBundesland = bundesland }
                    .padding(vertical = Dimens.SpaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (bundesland == selectedBundesland),
                    onClick = null
                )

                Spacer(modifier = Modifier.width(Dimens.SpaceMedium))

                Text(
                    text = bundesland.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        CommonButton(
            text = strings.languageSelectContinue,
            onClick = { onBundeslandSelected(selectedBundesland.name) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.ScreenPaddingBottom))
    }
}
