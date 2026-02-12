package org.igo.lidtrainer.core.time

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface TimeProvider {
    fun now(): Instant
    fun nowEpochMillis(): Long
    fun nowEpochSeconds(): Long
}

@OptIn(ExperimentalTime::class)
class SystemTimeProvider : TimeProvider {

    override fun now(): Instant = Clock.System.now()

    override fun nowEpochMillis(): Long =
        now().toEpochMilliseconds()

    override fun nowEpochSeconds(): Long =
        nowEpochMillis() / 1000L
}
