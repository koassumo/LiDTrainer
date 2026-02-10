package org.igo.lidtrainer.domain.strings

interface AppStrings {
    // App
    val appName: String

    // Language Select Screen
    val languageSelectTitle: String
    val languageSelectPrompt: String
    val languageSelectContinue: String

    // Dashboard Screen
    val dashboardTitle: String
    val studyQuestions: String
    val practiceTest: String
    val questionsAnswered: String
    val correctAnswers: String
    val incorrectAnswers: String
    val notAnswered: String
    val progressLabel: String

    // Learn Screen
    val learnTitle: String
    val nextQuestion: String
    val previousQuestion: String
    val showAnswer: String
    val questionNumber: String

    // Settings Screen
    val settingsTitle: String
    val themeSection: String
    val systemTheme: String
    val lightTheme: String
    val darkTheme: String
    val languageSection: String
    val languageEn: String
    val languageRu: String
    val languageDe: String

    // Common
    val backButtonTooltip: String
    val loading: String
    val okButton: String
    val cancelButton: String
    val errorGeneral: String

    // Exit Dialog
    val exitDialogTitle: String
    val exitDialogMessage: String
    val exitDialogConfirm: String
    val exitDialogCancel: String
}
