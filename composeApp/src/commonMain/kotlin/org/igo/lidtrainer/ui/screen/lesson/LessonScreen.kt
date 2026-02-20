package org.igo.lidtrainer.ui.screen.lesson

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.IntOffset

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.igo.lidtrainer.domain.model.LessonFilter
import org.igo.lidtrainer.domain.model.Note
import org.igo.lidtrainer.ui.common.CommonCard
import org.igo.lidtrainer.ui.common.QuestionImage
import org.igo.lidtrainer.ui.common.Dimens
import org.igo.lidtrainer.ui.common.LocalTopBarState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.igo.lidtrainer.ui.theme.answerCardBorder
import org.igo.lidtrainer.ui.theme.answerCardBackground
import org.igo.lidtrainer.ui.theme.answerCardText
import org.igo.lidtrainer.ui.theme.answerStripDefault
import org.igo.lidtrainer.ui.theme.correctBadge
import org.igo.lidtrainer.ui.theme.incorrectBadge
import org.igo.lidtrainer.ui.theme.correctAnswerBackground
import org.igo.lidtrainer.ui.theme.correctAnswerStrip
import org.igo.lidtrainer.ui.theme.correctAnswerText
import org.igo.lidtrainer.ui.theme.incorrectAnswerBackground
import org.igo.lidtrainer.ui.theme.incorrectAnswerStrip
import org.igo.lidtrainer.ui.theme.incorrectAnswerText
import org.igo.lidtrainer.ui.theme.LocalAppStrings
import org.igo.lidtrainer.ui.theme.favoriteStar
import org.igo.lidtrainer.ui.theme.myBarDivider
import org.igo.lidtrainer.ui.theme.translationToggleOffBackground
import org.igo.lidtrainer.ui.theme.translationToggleOffText
import org.igo.lidtrainer.ui.theme.translationToggleOnBackground
import org.igo.lidtrainer.ui.theme.translationText
import org.igo.lidtrainer.ui.theme.translationToggleOnText

@Composable
fun LessonScreen(
    viewModel: LessonViewModel,
    onNavigateBack: () -> Unit
) {
    val topBar = LocalTopBarState.current
    val strings = LocalAppStrings.current

    val notes by viewModel.notes.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val lessonFilter by viewModel.lessonFilter.collectAsState()
    val clickedAnswers by viewModel.clickedAnswers.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()
    val showCorrectImmediately by viewModel.showCorrectImmediately.collectAsState()
    val isTranslationAvailable by viewModel.isTranslationAvailable.collectAsState()
    val showSwipeHint by viewModel.showSwipeHint.collectAsState()
    val contentLanguageCode by viewModel.contentLanguageCode.collectAsState()

    val totalCount = notes.size

    topBar.canNavigateBack = true
    topBar.onNavigateBack = onNavigateBack

    // Кнопка-тоггл перевода в верхнем баре (левее шестерёнки настроек)
    topBar.extraActions = if (isTranslationAvailable && lessonFilter != LessonFilter.PRACTICE_TEST) {
        {
            TranslationToggleButton(
                languageCode = contentLanguageCode,
                isEnabled = showTranslation,
                onClick = { viewModel.toggleTranslation() }
            )
        }
    } else null

    // Сбрасываем при уходе с экрана
    DisposableEffect(Unit) {
        onDispose {
            topBar.titleContent = null
            topBar.extraActions = null
            topBar.bottomBarContent = null
        }
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
            pagerState.scrollToPage(currentIndex)
        }
    }

    // Заголовок в верхнем баре — просто текст, без счётчика
    topBar.title = strings.lessonTitle
    topBar.titleContent = null

    // Данные для нижнего бара (счётчик страниц)
    val currentPageNumber = pagerState.currentPage + 1
    val currentNoteForTitle = notes.getOrNull(pagerState.currentPage)

    val badgeColor = when (currentNoteForTitle?.isAnsweredCorrectly) {
        true -> MaterialTheme.colorScheme.correctBadge
        false -> MaterialTheme.colorScheme.incorrectBadge
        null -> MaterialTheme.colorScheme.surfaceVariant
    }
    val badgeTextColor = when (currentNoteForTitle?.isAnsweredCorrectly) {
        true -> MaterialTheme.colorScheme.correctAnswerText
        false -> MaterialTheme.colorScheme.incorrectAnswerText
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    var showQuestionGrid by remember { mutableStateOf(false) }

    // Нижний бар со счётчиком страниц (через Scaffold bottomBar)
    topBar.bottomBarContent = {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.myBarDivider)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showQuestionGrid = true }
                    .padding(vertical = Dimens.SpaceSmall)
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
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showQuestionGrid) {
        QuestionGridBottomSheet(
            notes = notes,
            currentPage = pagerState.currentPage,
            lessonFilter = lessonFilter,
            onQuestionSelected = { index ->
                showQuestionGrid = false
                viewModel.setCurrentIndex(index)
            },
            onDismiss = { showQuestionGrid = false }
        )
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
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                // Звёздочка избранного (справа)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.ScreenPaddingSides)
                        .padding(top = Dimens.ScreenPaddingTop),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleFavorite(currentNote.id) }
                    ) {
                        Icon(
                            imageVector = if (currentNote.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (currentNote.isFavorite) MaterialTheme.colorScheme.favoriteStar
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Картинка вопроса (если есть)
                if (currentNote.imageUrl != null) {
                    QuestionImage(
                        imageKey = currentNote.imageUrl,
                        modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides)
                    )
                    Spacer(Modifier.height(Dimens.SpaceMedium))
                } else {
                    Spacer(Modifier.height(Dimens.SpaceSmall))
                }

                // Текст вопроса на немецком
                Text(
                    text = currentNote.questionTextDe,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = Dimens.QuizQuestionTextSize),
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides)
                )

                // Перевод на родном языке
                if (showTranslation && currentNote.questionTextNative.isNotBlank()) {
                    Spacer(Modifier.height(Dimens.SpaceSmall))
                    Text(
                        text = currentNote.questionTextNative,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = Dimens.QuizTranslationTextSize),
                        color = MaterialTheme.colorScheme.translationText,
                        modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides)
                    )
                }

                Spacer(Modifier.height(Dimens.SpaceMedium))

                // 4 варианта ответа
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingSides),
                    verticalArrangement = Arrangement.spacedBy(Dimens.AnswerCardSpacing)
                ) {
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
        !isHighlighted -> MaterialTheme.colorScheme.answerCardBackground
        isCorrect -> MaterialTheme.colorScheme.correctAnswerBackground
        else -> MaterialTheme.colorScheme.incorrectAnswerBackground
    }

    val textColor = when {
        !isHighlighted -> MaterialTheme.colorScheme.answerCardText
        isCorrect -> MaterialTheme.colorScheme.correctAnswerText
        else -> MaterialTheme.colorScheme.incorrectAnswerText
    }

    val borderColor = MaterialTheme.colorScheme.answerCardBorder

    val stripColor = when {
        !isHighlighted -> MaterialTheme.colorScheme.answerStripDefault
        isCorrect -> MaterialTheme.colorScheme.correctAnswerStrip
        else -> MaterialTheme.colorScheme.incorrectAnswerStrip
    }

    CommonCard(
        containerColor = containerColor,
        borderColor = borderColor,
        cornerRadius = Dimens.AnswerCardCornerRadius,
        elevation = Dimens.CommonCardElevation,
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Цветная полоска слева со скруглёнными углами только слева
            Box(
                modifier = Modifier
                    .width(Dimens.AnswerCardStripWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(
                        topStart = Dimens.AnswerCardCornerRadius,
                        bottomStart = Dimens.AnswerCardCornerRadius
                    ))
                    .background(stripColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimens.CommonCardContentPadding)
            ) {
                Text(
                    text = "$index. $textDe",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = Dimens.QuizAnswerTextSize),
                    color = textColor
                )
                if (textNative.isNotBlank() && showTranslation) {
                    Spacer(Modifier.height(Dimens.SpaceSmall / 2))
                    Text(
                        text = textNative,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = Dimens.QuizTranslationTextSize),
                        color = MaterialTheme.colorScheme.translationText
                    )
                }
            }
        }
    }
}

@Composable
private fun TranslationToggleButton(
    languageCode: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isEnabled) {
        MaterialTheme.colorScheme.translationToggleOnBackground
    } else {
        MaterialTheme.colorScheme.translationToggleOffBackground
    }
    val textColor = if (isEnabled) {
        MaterialTheme.colorScheme.translationToggleOnText
    } else {
        MaterialTheme.colorScheme.translationToggleOffText
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(Dimens.TranslationToggleCornerRadius)
            )
            .clickable { onClick() }
            .padding(
                horizontal = Dimens.TranslationTogglePaddingHorizontal,
                vertical = Dimens.TranslationTogglePaddingVertical
            )
    ) {
        Text(
            text = languageCode.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionGridBottomSheet(
    notes: List<Note>,
    currentPage: Int,
    lessonFilter: LessonFilter,
    onQuestionSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Разделяем на секции: пара (заголовок, список (originalIndex, note))
    val sections = remember(notes, lessonFilter) {
        when (lessonFilter) {
            LessonFilter.GENERAL -> listOf(
                strings.generalQuestions to notes.mapIndexed { i, n -> i to n }
            )
            LessonFilter.REGIONAL -> listOf(
                strings.regionalQuestions to notes.mapIndexed { i, n -> i to n }
            )
            LessonFilter.ALL, LessonFilter.FAVORITES, LessonFilter.PRACTICE_TEST -> {
                val general = notes.mapIndexedNotNull { i, n ->
                    if (n.category == "GENERAL") i to n else null
                }
                val regional = notes.mapIndexedNotNull { i, n ->
                    if (n.category != "GENERAL") i to n else null
                }
                buildList {
                    if (general.isNotEmpty()) add(strings.generalQuestions to general)
                    if (regional.isNotEmpty()) add(strings.regionalQuestions to regional)
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            contentPadding = PaddingValues(Dimens.SpaceMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            sections.forEach { (title, indexedNotes) ->
                // Заголовок секции на всю ширину
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            top = Dimens.SpaceSmall,
                            bottom = Dimens.SpaceSmall / 2
                        )
                    )
                }

                items(indexedNotes.size) { i ->
                    val (originalIndex, note) = indexedNotes[i]
                    val isCurrent = originalIndex == currentPage

                    val bgColor = when (note.isAnsweredCorrectly) {
                        true -> MaterialTheme.colorScheme.correctBadge
                        false -> MaterialTheme.colorScheme.incorrectBadge
                        null -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val txtColor = when (note.isAnsweredCorrectly) {
                        true -> MaterialTheme.colorScheme.correctAnswerText
                        false -> MaterialTheme.colorScheme.incorrectAnswerText
                        null -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                color = bgColor,
                                shape = RoundedCornerShape(Dimens.SpaceSmall / 2)
                            )
                            .then(
                                if (isCurrent) Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(Dimens.SpaceSmall / 2)
                                ) else Modifier
                            )
                            .clickable { onQuestionSelected(originalIndex) }
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
    }
}
