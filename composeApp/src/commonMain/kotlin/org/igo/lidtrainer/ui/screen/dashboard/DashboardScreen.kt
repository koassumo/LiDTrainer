package org.igo.lidtrainer.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.igo.lidtrainer.ui.common.CommonCard
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings
import org.igo.lidtrainer.ui.theme.answerCardBackground
import org.igo.lidtrainer.ui.theme.answerCardText
import org.igo.lidtrainer.ui.theme.answerStripDefault

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
        DashboardCard(
            text = strings.allQuestions,
            count = totalQuestions,
            onClick = onAllClick
        )
        Spacer(Modifier.height(Dimens.DashboardCardSpacing))
        DashboardCard(
            text = strings.generalQuestions,
            count = generalCount,
            onClick = onGeneralClick
        )
        Spacer(Modifier.height(Dimens.DashboardCardSpacing))
        DashboardCard(
            text = strings.regionalQuestions,
            count = regionalCount,
            onClick = onRegionalClick
        )
        Spacer(Modifier.height(Dimens.DashboardCardSpacing))
        DashboardCard(
            text = strings.favorites,
            count = favoritesCount,
            onClick = onFavoritesClick
        )
        Spacer(Modifier.height(Dimens.SpaceLarge + Dimens.SpaceMedium))
        DashboardCard(
            text = strings.practiceTest,
            count = 33,
            onClick = onPracticeTestClick
        )
        Spacer(Modifier.height(Dimens.ScreenPaddingBottom))
    }
}

@Composable
private fun DashboardCard(
    text: String,
    count: Long,
    onClick: () -> Unit
) {
    CommonCard(
        containerColor = MaterialTheme.colorScheme.answerCardBackground,
        borderColor = null,
        cornerRadius = Dimens.DashboardCardCornerRadius,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Цветная полоска слева
            Box(
                modifier = Modifier
                    .width(Dimens.DashboardCardStripWidth)
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimens.DashboardCardCornerRadius,
                            bottomStart = Dimens.DashboardCardCornerRadius
                        )
                    )
                    .background(MaterialTheme.colorScheme.answerStripDefault)
            )
            // Место под картинку (пока пустое)
            Box(
                modifier = Modifier
                    .width(Dimens.SpaceExtraLarge)
                    .fillMaxHeight()
            )
            // Текст
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimens.CommonCardContentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = Dimens.DashboardCardTextSize),
                    color = MaterialTheme.colorScheme.answerCardText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = Dimens.DashboardCardTextSize),
                    color = MaterialTheme.colorScheme.answerCardText
                )
            }
        }
    }
}
