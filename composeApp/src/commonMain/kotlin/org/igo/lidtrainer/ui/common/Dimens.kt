package org.igo.lidtrainer.ui.common

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Централизованные размеры и отступы для UI компонентов.
 * Никаких хардкодов .dp в экранах — только Dimens.
 */
object Dimens {

    // ==================== Screen / Display ====================
    val ScreenPaddingSides = 16.dp
    val ScreenPaddingTop = 16.dp
    val ScreenPaddingBottom = 16.dp

    // ==================== CommonCard ====================
    val CommonCardContentPadding = 16.dp
    val CommonCardCornerRadius = 16.dp
    val CommonCardElevation = 0.dp
    val CommonCardBorderWidth = 1.dp

    // ==================== CommonButton ====================
    val CommonButtonHeight = 48.dp
    val CommonButtonCornerRadius = 24.dp

    // ==================== Spacing ====================
    val SpaceSmall = 8.dp
    val SpaceMedium = 16.dp
    val SpaceLarge = 24.dp
    val SpaceExtraLarge = 32.dp

    // ==================== Components ====================
    val IconSizeSmall = 20.dp
    val IconSizeMedium = 24.dp
    val IconSizeLarge = 28.dp
    val ProgressIndicatorStrokeWidth = 2.dp

    // ==================== Input Fields ====================
    val InputFieldCornerRadius = 8.dp

    // ==================== Swipe Hint ====================
    val SwipeHintCircleSize = 56.dp
    val SwipeHintIconSize = 28.dp
    val SwipeHintBottomPadding = 80.dp

    // ==================== Answer Card ====================
    // Меняй здесь: закругление углов карточки ответа
    val AnswerCardCornerRadius = 12.dp
    // Меняй здесь: ширина цветной полоски слева
    val AnswerCardStripWidth = 15.dp
    // Меняй здесь: отступы между карточками ответов
    val AnswerCardSpacing = 8.dp

    // ==================== Translation Toggle ====================
    // Меняй здесь: закругление углов кнопки-тоггла перевода
    val TranslationToggleCornerRadius = 8.dp
    // Меняй здесь: горизонтальный отступ внутри кнопки-тоггла
    val TranslationTogglePaddingHorizontal = 8.dp
    // Меняй здесь: вертикальный отступ внутри кнопки-тоггла
    val TranslationTogglePaddingVertical = 4.dp

    // ==================== Dashboard Card ====================
    val DashboardCardCornerRadius = 12.dp
    val DashboardCardStripWidth = 10.dp
    val DashboardCardSpacing = 8.dp
    val DashboardCardTextSize = 18.sp

    // ==================== Other ====================
    val BorderWidthStandard = 1.dp

    // ==================== Text Sizes ====================
    val TextSizeTitle = 20.sp
    val TextSizeBody = 16.sp

    // ==================== Quiz Text Sizes ====================
    val QuizQuestionTextSize = 18.sp
    val QuizAnswerTextSize = 18.sp
    val QuizTranslationTextSize = 16.sp
}
