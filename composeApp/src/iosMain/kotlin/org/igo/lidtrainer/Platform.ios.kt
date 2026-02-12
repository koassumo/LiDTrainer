package org.igo.lidtrainer

import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun getSystemLanguageCode(): String {
    val languages = NSUserDefaults.standardUserDefaults.objectForKey("AppleLanguages") as? List<*>
    val firstLanguage = languages?.firstOrNull() as? String ?: "en"
    // AppleLanguages возвращает вида "ru-RU", "en-US" — берём только первую часть
    return firstLanguage.split("-").first().lowercase()
}