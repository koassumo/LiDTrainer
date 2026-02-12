package org.igo.lidtrainer.di

import org.igo.lidtrainer.ui.screen.lesson.LessonViewModel
import org.igo.lidtrainer.ui.screen.main.MainViewModel
import org.igo.lidtrainer.ui.screen.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::LessonViewModel)
    viewModelOf(::SettingsViewModel)
}
