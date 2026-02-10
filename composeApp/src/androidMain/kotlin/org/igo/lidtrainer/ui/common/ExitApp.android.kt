package org.igo.lidtrainer.ui.common

import android.os.Process

actual fun exitApp() {
    Process.killProcess(Process.myPid())
}
