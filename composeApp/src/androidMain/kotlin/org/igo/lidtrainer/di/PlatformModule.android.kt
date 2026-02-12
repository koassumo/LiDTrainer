package org.igo.lidtrainer.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.igo.lidtrainer.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Settings> {
        val sharedPrefs = androidContext().getSharedPreferences("lidtrainer_settings", 0)
        SharedPreferencesSettings(sharedPrefs)
    }

    single<SqlDriver> {
        AndroidSqliteDriver(AppDatabase.Schema, androidContext(), "lidtrainer.db")
    }
}
