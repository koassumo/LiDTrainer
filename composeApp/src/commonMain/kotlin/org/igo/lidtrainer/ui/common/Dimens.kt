package org.igo.lidtrainer.ui.common

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Централизованные размеры и отступы для UI компонентов.
 * Никаких хардкодов .dp в экранах — только Dimens.
 */
object Dimens {

    // ==================== Screen / Display ====================
    val ScreenPaddingSides = 12.dp
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

    // ==================== Other ====================
    val BorderWidthStandard = 1.dp

    // ==================== Text Sizes ====================
    val TextSizeTitle = 20.sp
    val TextSizeBody = 16.sp
}
