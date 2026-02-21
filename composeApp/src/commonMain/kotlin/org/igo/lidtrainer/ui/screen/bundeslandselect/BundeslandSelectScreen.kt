package org.igo.lidtrainer.ui.screen.bundeslandselect

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
import org.igo.lidtrainer.domain.model.Bundesland
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun BundeslandSelectScreen(
    selectedBundesland: String,
    onBundeslandSelected: (String) -> Unit,
    title: String? = null,
    canNavigateBack: Boolean = false,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (title != "") {
        val topBar = LocalTopBarState.current
        val strings = LocalAppStrings.current
        topBar.title = title ?: strings.bundeslandSelectTitle
        topBar.canNavigateBack = canNavigateBack
        topBar.onNavigateBack = onNavigateBack ?: {}
    }

    val bundeslaender = Bundesland.entries

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPaddingSides)
        ) {
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        bundeslaender.forEach { bundesland ->
            ListItem(
                modifier = Modifier.clickable { onBundeslandSelected(bundesland.name) },
                headlineContent = {
                    Text(
                        text = bundesland.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingContent = {
                    RadioButton(
                        selected = (bundesland.name == selectedBundesland),
                        onClick = null
                    )
                }
            )
            HorizontalDivider()
        }
        }
    }
}
