package org.igo.lidtrainer.di

import app.cash.sqldelight.db.SqlDriver
import org.igo.lidtrainer.core.time.SystemTimeProvider
import org.igo.lidtrainer.core.time.TimeProvider
import org.igo.lidtrainer.data.repository.NoteRepositoryImpl
import org.igo.lidtrainer.data.repository.SettingsRepositoryImpl
import org.igo.lidtrainer.db.AppDatabase
import org.igo.lidtrainer.domain.rep_interface.NoteRepository
import org.igo.lidtrainer.domain.rep_interface.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SystemTimeProvider) bind TimeProvider::class

    single<AppDatabase> {
        val driver = get<SqlDriver>()
        AppDatabase(driver)
    }

    singleOf(::NoteRepositoryImpl) bind NoteRepository::class

    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
}
