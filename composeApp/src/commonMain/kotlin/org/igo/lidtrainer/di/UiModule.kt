package org.igo.lidtrainer.di

import org.igo.lidtrainer.ui.screen.learn.LearnViewModel
import org.igo.lidtrainer.ui.screen.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val uiModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::LearnViewModel)
}
