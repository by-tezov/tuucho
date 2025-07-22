package com.tezov.tuucho.core.domain._system

import android.util.Log


fun logAll(value: Any?) {
    val output = value?.toString() ?: "null"
    logLong(output)
}

private fun logLong(message: String) {
    val maxLogSize = 4000
    var i = 0
    while (i < message.length) {
        val end = (i + maxLogSize).coerceAtMost(message.length)
        Log.d("", message.substring(i, end))
        i += maxLogSize
    }
}