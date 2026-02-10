package org.igo.lidtrainer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import lidtrainer.composeapp.generated.resources.Res
import lidtrainer.composeapp.generated.resources.*
import org.igo.lidtrainer.domain.strings.AppStrings
import org.jetbrains.compose.resources.stringResource

data class AppStringsImpl(
    override val appName: String,
    override val languageSelectTitle: String,
    override val languageSelectPrompt: String,
    override val languageSelectContinue: String,
    override val dashboardTitle: String,
    override val studyQuestions: String,
    override val practiceTest: String,
    override val questionsAnswered: String,
    override val correctAnswers: String,
    override val incorrectAnswers: String,
    override val notAnswered: String,
    override val progressLabel: String,
    override val learnTitle: String,
    override val nextQuestion: String,
    override val previousQuestion: String,
    override val showAnswer: String,
    override val questionNumber: String,
    override val settingsTitle: String,
    override val themeSection: String,
    override val systemTheme: String,
    override val lightTheme: String,
    override val darkTheme: String,
    override val languageSection: String,
    override val languageEn: String,
    override val languageRu: String,
    override val languageDe: String,
    override val backButtonTooltip: String,
    override val loading: String,
    override val okButton: String,
    override val cancelButton: String,
    override val errorGeneral: String,
    override val exitDialogTitle: String,
    override val exitDialogMessage: String,
    override val exitDialogConfirm: String,
    override val exitDialogCancel: String,
) : AppStrings

val LocalAppStrings = compositionLocalOf<AppStrings> {
    error("AppStrings not provided")
}

@Composable
fun rememberAppStrings(languageConfig: AppLanguageConfig): AppStrings {
    return AppStringsImpl(
        appName = stringResource(Res.string.app_name),
        languageSelectTitle = stringResource(Res.string.language_select_title),
        languageSelectPrompt = stringResource(Res.string.language_select_prompt),
        languageSelectContinue = stringResource(Res.string.language_select_continue),
        dashboardTitle = stringResource(Res.string.dashboard_title),
        studyQuestions = stringResource(Res.string.study_questions),
        practiceTest = stringResource(Res.string.practice_test),
        questionsAnswered = stringResource(Res.string.questions_answered),
        correctAnswers = stringResource(Res.string.correct_answers),
        incorrectAnswers = stringResource(Res.string.incorrect_answers),
        notAnswered = stringResource(Res.string.not_answered),
        progressLabel = stringResource(Res.string.progress_label),
        learnTitle = stringResource(Res.string.learn_title),
        nextQuestion = stringResource(Res.string.next_question),
        previousQuestion = stringResource(Res.string.previous_question),
        showAnswer = stringResource(Res.string.show_answer),
        questionNumber = stringResource(Res.string.question_number),
        settingsTitle = stringResource(Res.string.settings_title),
        themeSection = stringResource(Res.string.theme_section),
        systemTheme = stringResource(Res.string.system_theme),
        lightTheme = stringResource(Res.string.light_theme),
        darkTheme = stringResource(Res.string.dark_theme),
        languageSection = stringResource(Res.string.language_section),
        languageEn = stringResource(Res.string.language_en),
        languageRu = stringResource(Res.string.language_ru),
        languageDe = stringResource(Res.string.language_de),
        backButtonTooltip = stringResource(Res.string.back_button_tooltip),
        loading = stringResource(Res.string.loading),
        okButton = stringResource(Res.string.ok_button),
        cancelButton = stringResource(Res.string.cancel_button),
        errorGeneral = stringResource(Res.string.error_general),
        exitDialogTitle = stringResource(Res.string.exit_dialog_title),
        exitDialogMessage = stringResource(Res.string.exit_dialog_message),
        exitDialogConfirm = stringResource(Res.string.exit_dialog_confirm),
        exitDialogCancel = stringResource(Res.string.exit_dialog_cancel),
    )
}
