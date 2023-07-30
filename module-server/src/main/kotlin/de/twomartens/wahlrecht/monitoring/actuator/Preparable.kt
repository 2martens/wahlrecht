package de.twomartens.wahlrecht.monitoring.actuator

import java.io.Closeable

fun interface Preparable {
    fun prepare(): Closeable
}
