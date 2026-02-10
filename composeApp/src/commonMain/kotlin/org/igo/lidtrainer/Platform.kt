package org.igo.lidtrainer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform