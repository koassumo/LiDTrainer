package org.igo.lidtrainer

import android.app.Application
import org.igo.lidtrainer.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class LiDTrainerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@LiDTrainerApp)
        }
    }
}
