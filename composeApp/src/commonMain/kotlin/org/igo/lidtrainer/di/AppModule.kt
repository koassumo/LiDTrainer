package org.igo.lidtrainer.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            dataModule,
            domainModule,
            uiModule,
            platformModule
        )
    }
}

val appModule = module {
    // General app utilities
}
