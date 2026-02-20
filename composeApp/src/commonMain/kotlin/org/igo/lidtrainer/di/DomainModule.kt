package org.igo.lidtrainer.di

import org.igo.lidtrainer.domain.usecase.CheckAndUpdatePackUseCase
import org.igo.lidtrainer.domain.usecase.LoadNotesFromJsonUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::LoadNotesFromJsonUseCase)
    factoryOf(::CheckAndUpdatePackUseCase)
}
