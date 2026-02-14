package org.igo.lidtrainer.domain.strings

interface AppStrings {
    // App
    val appName: String

    // Language Select Screen
    val languageSelectTitle: String
    val languageSelectPrompt: String
    val languageSelectContinue: String

    // Bundesland Select Screen
    val bundeslandSelectTitle: String
    val bundeslandSelectPrompt: String
    val bundeslandSection: String

    // Dashboard Screen
    val dashboardTitle: String
    val studyQuestions: String
    val allQuestions: String
    val generalQuestions: String
    val regionalQuestions: String
    val favorites: String
    val practiceTest: String
    val questionsAnswered: String
    val correctAnswers: String
    val incorrectAnswers: String
    val notAnswered: String
    val progressLabel: String

    // Lesson Screen
    val lessonTitle: String
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
    val languageContentSection: String
    val languageEn: String
    val languageRu: String
    val languageDe: String
    val showCorrectImmediately: String

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
