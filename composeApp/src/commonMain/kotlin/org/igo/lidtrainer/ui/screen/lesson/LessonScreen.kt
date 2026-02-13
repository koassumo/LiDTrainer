package org.igo.lidtrainer.ui.screen.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import org.igo.lidtrainer.ui.common.CommonCard
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import org.igo.lidtrainer.ui.theme.CorrectAnswerBackground
import org.igo.lidtrainer.ui.theme.CorrectAnswerText
import org.igo.lidtrainer.ui.theme.IncorrectAnswerBackground
import org.igo.lidtrainer.ui.theme.IncorrectAnswerText
import org.igo.lidtrainer.ui.theme.LocalAppStrings

@Composable
fun LessonScreen(
    viewModel: LessonViewModel,
    onNavigateBack: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    val notes by viewModel.notes.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val clickedAnswers by viewModel.clickedAnswers.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()
    val showCorrectImmediately by viewModel.showCorrectImmediately.collectAsState()
    val isTranslationAvailable by viewModel.isTranslationAvailable.collectAsState()

    val totalCount = notes.size

    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack

    if (totalCount == 0) {
        topBar.title = strings.lessonTitle
        return
    }

    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { totalCount }
    )

    // Pager → ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.setCurrentIndex(page)
        }
    }

    // ViewModel → Pager (for programmatic navigation)
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }

    // Update top bar title from pager
    topBar.title = "${pagerState.currentPage + 1} / $totalCount"

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val currentNote = notes[page]
        val noteClickedSet = clickedAnswers[currentNote.id] ?: emptySet()
        val revealAll = showCorrectImmediately && noteClickedSet.isNotEmpty()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = Dimens.ScreenPaddingSides,
                    vertical = Dimens.ScreenPaddingTop
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Переключатель перевода
            if (isTranslationAvailable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = showTranslation,
                        onCheckedChange = { viewModel.toggleTranslation() }
                    )
                }
                Spacer(Modifier.height(Dimens.SpaceSmall))
            }

            // Текст вопроса на немецком
            Text(
                text = currentNote.questionTextDe,
                style = MaterialTheme.typography.bodyLarge
            )

            // Перевод на родном языке
            if (showTranslation && currentNote.questionTextNative.isNotBlank()) {
                Spacer(Modifier.height(Dimens.SpaceSmall))
                Text(
                    text = currentNote.questionTextNative,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(Dimens.SpaceMedium))

            // 4 варианта ответа
            for (i in 1..4) {
                val textDe = when (i) {
                    1 -> currentNote.answer1De
                    2 -> currentNote.answer2De
                    3 -> currentNote.answer3De
                    else -> currentNote.answer4De
                }
                val textNative = when (i) {
                    1 -> currentNote.answer1Native
                    2 -> currentNote.answer2Native
                    3 -> currentNote.answer3Native
                    else -> currentNote.answer4Native
                }
                AnswerCard(
                    index = i,
                    textDe = textDe,
                    textNative = textNative,
                    isClicked = i in noteClickedSet,
                    isCorrect = currentNote.correctAnswerIndex == i,
                    showTranslation = showTranslation,
                    revealAll = revealAll,
                    onClick = { viewModel.onAnswerClick(i, currentNote.id) }
                )
                if (i < 4) Spacer(Modifier.height(Dimens.SpaceSmall))
            }

            Spacer(Modifier.height(Dimens.ScreenPaddingBottom))
        }
    }
}

@Composable
private fun AnswerCard(
    index: Int,
    textDe: String,
    textNative: String,
    isClicked: Boolean,
    isCorrect: Boolean,
    showTranslation: Boolean,
    revealAll: Boolean,
    onClick: () -> Unit
) {
    // Показываем подсветку если: 1) этот ответ был нажат, ИЛИ 2) revealAll включён
    val isHighlighted = isClicked || revealAll

    val containerColor = when {
        !isHighlighted -> MaterialTheme.colorScheme.surface
        isCorrect -> CorrectAnswerBackground
        else -> IncorrectAnswerBackground
    }

    val textColor = when {
        !isHighlighted -> MaterialTheme.colorScheme.onSurface
        isCorrect -> CorrectAnswerText
        else -> IncorrectAnswerText
    }

    val borderColor = when {
        !isHighlighted -> MaterialTheme.colorScheme.outlineVariant
        isCorrect -> CorrectAnswerText
        else -> IncorrectAnswerText
    }

    CommonCard(
        containerColor = containerColor,
        borderColor = borderColor,
        onClick = onClick
    ) {
        Text(
            text = "$index. $textDe",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        if (textNative.isNotBlank() && showTranslation) {
            Spacer(Modifier.height(Dimens.SpaceSmall / 2))
            Text(
                text = textNative,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}
