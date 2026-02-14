package org.igo.lidtrainer.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.ui.common.CommonButton
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun DashboardScreen(
    totalQuestions: Long,
    generalCount: Long,
    regionalCount: Long,
    favoritesCount: Long,
    onAllClick: () -> Unit,
    onGeneralClick: () -> Unit,
    onRegionalClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPracticeTestClick: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    topBar.title = strings.dashboardTitle
    topBar.canNavigateBack = false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = Dimens.ScreenPaddingSides,
                vertical = Dimens.ScreenPaddingTop
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonButton(
            text = "${strings.allQuestions}: $totalQuestions",
            onClick = onAllClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimens.SpaceMedium))
        CommonButton(
            text = "${strings.generalQuestions}: $generalCount",
            onClick = onGeneralClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimens.SpaceMedium))
        CommonButton(
            text = "${strings.regionalQuestions}: $regionalCount",
            onClick = onRegionalClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimens.SpaceMedium))
        CommonButton(
            text = "${strings.favorites}: $favoritesCount",
            onClick = onFavoritesClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimens.SpaceLarge + Dimens.SpaceMedium))
        CommonButton(
            text = "${strings.practiceTest}: 33",
            onClick = onPracticeTestClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Dimens.ScreenPaddingBottom))
    }
}
