package org.igo.lidtrainer.ui.screen.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.LocalAppStrings
import org.igo.lidtrainer.ui.theme.chartCorrect
import org.igo.lidtrainer.ui.theme.chartIncorrect
import org.igo.lidtrainer.ui.theme.chartNotAnswered
import org.igo.lidtrainer.ui.theme.correctAnswerText
import org.igo.lidtrainer.ui.theme.correctBadge
import org.igo.lidtrainer.ui.theme.incorrectAnswerText
import org.igo.lidtrainer.ui.theme.incorrectBadge

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    onNavigateBack: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    topBar.title = strings.statisticsTitle
    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack

    val notes by viewModel.notes.collectAsState()

    // Считаем статистику прямо из notes — единый источник данных для круга и сетки
    val total = notes.size.toLong()
    val correctCount = notes.count { it.isAnsweredCorrectly == true }.toLong()
    val incorrectCount = notes.count { it.isAnsweredCorrectly == false }.toLong()
    val notAnsweredCount = total - correctCount - incorrectCount

    // Насыщенные цвета для диаграммы и легенды
    val chartCorrectColor = MaterialTheme.colorScheme.chartCorrect
    val chartIncorrectColor = MaterialTheme.colorScheme.chartIncorrect
    val chartNotAnsweredColor = MaterialTheme.colorScheme.chartNotAnswered

    // Бледные цвета для фона ячеек сетки
    val badgeCorrectColor = MaterialTheme.colorScheme.correctBadge
    val badgeIncorrectColor = MaterialTheme.colorScheme.incorrectBadge
    val badgeNotAnsweredColor = MaterialTheme.colorScheme.surfaceVariant
    val correctTextColor = MaterialTheme.colorScheme.correctAnswerText
    val incorrectTextColor = MaterialTheme.colorScheme.incorrectAnswerText
    val notAnsweredTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val sections = remember(notes) {
        val general = notes.filter { it.category == "GENERAL" }
        val regional = notes.filter { it.category != "GENERAL" }
        buildList {
            if (general.isNotEmpty()) add("general" to general)
            if (regional.isNotEmpty()) add("regional" to regional)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Вычисляем размер ячейки один раз: (ширина - paddings - gaps) / 8
        val cellSize = (maxWidth - Dimens.ScreenPaddingSides * 2 - Dimens.SpaceSmall * 7) / 8

    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        contentPadding = PaddingValues(
            start = Dimens.ScreenPaddingSides,
            end = Dimens.ScreenPaddingSides,
            top = Dimens.ScreenPaddingTop,
            bottom = Dimens.ScreenPaddingBottom
        ),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall),
        modifier = Modifier.fillMaxSize()
    ) {
        // Donut chart
        item(key = "donut", span = { GridItemSpan(maxLineSpan) }, contentType = "chart") {
            PieChart(
                correct = correctCount,
                incorrect = incorrectCount,
                notAnswered = notAnsweredCount,
                total = total,
                correctColor = chartCorrectColor,
                incorrectColor = chartIncorrectColor,
                notAnsweredColor = chartNotAnsweredColor
            )
        }

        // Legend
        item(key = "legend", span = { GridItemSpan(maxLineSpan) }, contentType = "legend") {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpaceLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LegendRow(color = chartCorrectColor, label = strings.correctAnswers, count = correctCount)
                LegendRow(color = chartIncorrectColor, label = strings.incorrectAnswers, count = incorrectCount)
                LegendRow(color = chartNotAnsweredColor, label = strings.notAnswered, count = notAnsweredCount)
            }
        }

        // Question grid sections
        sections.forEach { (sectionKey, sectionNotes) ->
            item(key = "header_$sectionKey", span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
                Text(
                    text = if (sectionKey == "general") strings.generalQuestions else strings.regionalQuestions,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        top = Dimens.SpaceSmall,
                        bottom = Dimens.SpaceSmall / 2
                    )
                )
            }

            items(
                count = sectionNotes.size,
                key = { sectionNotes[it].id },
                contentType = { "cell" }
            ) { i ->
                val note = sectionNotes[i]
                val bgColor = when (note.isAnsweredCorrectly) {
                    true -> badgeCorrectColor
                    false -> badgeIncorrectColor
                    null -> badgeNotAnsweredColor
                }
                val txtColor = when (note.isAnsweredCorrectly) {
                    true -> correctTextColor
                    false -> incorrectTextColor
                    null -> notAnsweredTextColor
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(cellSize)
                        .background(
                            color = bgColor,
                            shape = RoundedCornerShape(Dimens.SpaceSmall / 2)
                        )
                ) {
                    Text(
                        text = "${note.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = txtColor
                    )
                }
            }
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun PieChart(
    correct: Long,
    incorrect: Long,
    notAnswered: Long,
    total: Long,
    correctColor: Color,
    incorrectColor: Color,
    notAnsweredColor: Color
) {
    val correctPercent = if (total > 0) (correct * 100 / total).toInt() else 0
    val incorrectPercent = if (total > 0) (incorrect * 100 / total).toInt() else 0

    val correctSweep = if (total > 0) correct.toFloat() / total * 360f else 0f
    val incorrectSweep = if (total > 0) incorrect.toFloat() / total * 360f else 0f
    val notAnsweredSweep = 360f - correctSweep - incorrectSweep

    val chartSize = 160.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpaceMedium)
    ) {
        // Обёртка: проценты по углам + круг по центру
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            // Левый верхний — правильные
            Text(
                text = if (correctPercent > 0) "$correctPercent%" else "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = correctColor,
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.End
            )

            Spacer(Modifier.width(4.dp))

            // Круг
            Canvas(modifier = Modifier.size(chartSize)) {
                if (total == 0L) {
                    drawArc(
                        color = notAnsweredColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = true,
                        size = size
                    )
                    return@Canvas
                }

                // Правильные: от 12:00 ВЛЕВО (против часовой = отрицательный sweep)
                if (correctSweep > 0f) {
                    drawArc(
                        color = correctColor,
                        startAngle = -90f,
                        sweepAngle = -correctSweep,
                        useCenter = true,
                        size = size
                    )
                }

                // Неправильные: от 12:00 ВПРАВО (по часовой = положительный sweep)
                if (incorrectSweep > 0f) {
                    drawArc(
                        color = incorrectColor,
                        startAngle = -90f,
                        sweepAngle = incorrectSweep,
                        useCenter = true,
                        size = size
                    )
                }

                // Без ответа: остаток внизу
                if (notAnsweredSweep > 0f) {
                    drawArc(
                        color = notAnsweredColor,
                        startAngle = -90f + incorrectSweep,
                        sweepAngle = notAnsweredSweep,
                        useCenter = true,
                        size = size
                    )
                }
            }

            Spacer(Modifier.width(4.dp))

            // Правый верхний — неправильные
            Text(
                text = if (incorrectPercent > 0) "$incorrectPercent%" else "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = incorrectColor,
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String, count: Long) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(Dimens.SpaceSmall))
        Text(
            text = "$label: $count",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
