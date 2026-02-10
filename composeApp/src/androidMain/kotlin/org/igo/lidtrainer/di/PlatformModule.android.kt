package org.igo.lidtrainer.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<Settings> {
        val sharedPrefs = androidContext().getSharedPreferences("lidtrainer_settings", 0)
        SharedPreferencesSettings(sharedPrefs)
    }
}
