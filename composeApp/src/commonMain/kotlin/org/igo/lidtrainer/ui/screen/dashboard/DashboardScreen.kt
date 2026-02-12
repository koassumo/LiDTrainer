package org.igo.lidtrainer.ui.screen.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.igo.lidtrainer.ui.common.CommonButton
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun DashboardScreen(
    totalQuestions: Long,
    onAllQuestionsClick: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    topBar.title = strings.dashboardTitle
    topBar.canNavigateBack = false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = Dimens.ScreenPaddingSides,
                vertical = Dimens.ScreenPaddingTop
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        CommonButton(
            text = "${strings.studyQuestions}: $totalQuestions",
            onClick = onAllQuestionsClick
        )
    }
}
