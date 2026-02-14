package org.igo.lidtrainer.ui.screen.lesson

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.igo.lidtrainer.ui.common.CommonCard
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.igo.lidtrainer.ui.theme.AnswerStripDefault
import org.igo.lidtrainer.ui.theme.CorrectAnswerBackground
import org.igo.lidtrainer.ui.theme.CorrectAnswerStrip
import org.igo.lidtrainer.ui.theme.IncorrectAnswerStrip
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
    val firstAttemptResults by viewModel.firstAttemptResults.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()
    val showCorrectImmediately by viewModel.showCorrectImmediately.collectAsState()
    val isTranslationAvailable by viewModel.isTranslationAvailable.collectAsState()
    val showSwipeHint by viewModel.showSwipeHint.collectAsState()

    val totalCount = notes.size

    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack

    // Сбрасываем titleContent при уходе с экрана
    DisposableEffect(Unit) {
        onDispose { topBar.titleContent = null }
    }

    if (totalCount == 0) {
        topBar.title = strings.lessonTitle
        topBar.titleContent = null
        return
    }

    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { totalCount }
    )

    // Pager → ViewModel
    var initialPage by remember { mutableStateOf(pagerState.currentPage) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.setCurrentIndex(page)
            // Пользователь свайпнул (страница изменилась) — убираем подсказку навсегда
            if (page != initialPage && showSwipeHint) {
                viewModel.onSwipeHintSeen()
            }
        }
    }

    // ViewModel → Pager (for programmatic navigation)
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }

    // Update top bar title from pager
    val currentPageNumber = pagerState.currentPage + 1
    val currentNoteForTitle = notes.getOrNull(pagerState.currentPage)
    val firstAttemptResult = currentNoteForTitle?.let { firstAttemptResults[it.id] }

    val badgeColor = when (firstAttemptResult) {
        true -> CorrectAnswerBackground
        false -> IncorrectAnswerBackground
        null -> MaterialTheme.colorScheme.surfaceVariant
    }
    val badgeTextColor = when (firstAttemptResult) {
        true -> CorrectAnswerText
        false -> IncorrectAnswerText
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    topBar.titleContent = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = badgeColor,
                        shape = RoundedCornerShape(Dimens.SpaceSmall / 2)
                    )
                    .padding(
                        horizontal = Dimens.SpaceSmall,
                        vertical = Dimens.SpaceSmall / 2
                    )
            ) {
                Text(
                    text = "$currentPageNumber",
                    style = MaterialTheme.typography.titleMedium,
                    color = badgeTextColor
                )
            }
            Text(
                text = " / $totalCount",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    // Подсказка: текущая карточка + правильный ответ найден + пользователь ещё ни разу не свайпнул
    val currentNote = notes.getOrNull(pagerState.currentPage)
    val currentNoteClicked = currentNote?.let { clickedAnswers[it.id] } ?: emptySet()
    val correctFound = currentNote != null && currentNote.correctAnswerIndex in currentNoteClicked
    val shouldShowHint = showSwipeHint && correctFound

    // Анимация стрелки: отдельный Animatable для позиции (0f = правый край, 1f = 70% влево)
    val arrowProgress = remember { Animatable(0f) }
    // Прозрачность стрелки
    val arrowAlpha = remember { Animatable(0f) }
    // Ширина контейнера
    var containerWidthPx by remember { mutableStateOf(0) }

    // Nudge бесконечный: 3с пауза → стрелка тянет экран → стрелка пропадает → экран возвращается
    LaunchedEffect(shouldShowHint) {
        if (shouldShowHint && totalCount > 1) {
            delay(3000)
            while (true) {
                // Стрелка появляется и тянет экран параллельно
                coroutineScope {
                    launch { arrowAlpha.animateTo(1f, tween(400)) }
                    launch { arrowProgress.animateTo(1f, tween(1400)) }  // 50% экрана
                    launch {
                        delay(200) // Стрелка чуть опережает
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage,
                            pageOffsetFraction = 0.15f,
                            animationSpec = tween(1200)
                        )
                    }
                }
                // Стрелка исчезает — "отпустили"
                arrowAlpha.animateTo(0f, tween(400))
                arrowProgress.snapTo(0f) // Сброс позиции после полного исчезновения
                // Экран возвращается пружиной
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage,
                    pageOffsetFraction = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                delay(3000)
            }
        } else {
            // Сброс при выключении подсказки
            arrowAlpha.snapTo(0f)
            arrowProgress.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerWidthPx = it.width }
    ) {
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
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
            ) {
                // Переключатель перевода
                if (isTranslationAvailable) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.ScreenPaddingSides)
                            .padding(top = Dimens.ScreenPaddingTop),
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
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(
                        horizontal = Dimens.ScreenPaddingSides,
                        vertical = if (!isTranslationAvailable) Dimens.ScreenPaddingTop else 0.dp
                    )
                )

                // Перевод на родном языке
                if (showTranslation && currentNote.questionTextNative.isNotBlank()) {
                    Spacer(Modifier.height(Dimens.SpaceSmall))
                    Text(
                        text = currentNote.questionTextNative,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides)
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
                }

                Spacer(Modifier.height(Dimens.ScreenPaddingBottom))
            }
        }

        // Стрелка ← в полупрозрачном кружке, внизу экрана
        if (arrowAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimens.SpaceLarge, bottom = Dimens.SwipeHintBottomPadding)
                    .offset {
                        val distance = containerWidthPx * 0.5f
                        IntOffset(x = -(arrowProgress.value * distance).toInt(), y = 0)
                    }
                    .alpha(arrowAlpha.value)
                    .size(Dimens.SwipeHintCircleSize)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.SwipeHintIconSize),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
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

    val stripColor = when {
        !isHighlighted -> containerColor
        isCorrect -> CorrectAnswerStrip
        else -> IncorrectAnswerStrip
    }

    CommonCard(
        containerColor = containerColor,
        borderColor = null,
        cornerRadius = 0.dp,
        elevation = 0.dp,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.1f).fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(stripColor)
                )
                HorizontalDivider(color = if (isHighlighted) borderColor else MaterialTheme.colorScheme.surface)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.CommonCardContentPadding)
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
                HorizontalDivider(color = borderColor)
            }
        }
    }
}
