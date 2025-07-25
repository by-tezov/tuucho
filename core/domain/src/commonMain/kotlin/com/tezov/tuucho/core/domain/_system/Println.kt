package com.tezov.tuucho.core.domain._system

fun logAll(value: Any?) {
    val output = value?.toString() ?: "null"
    logLong(output)
}

private fun logLong(message: String) {
    val maxLogSize = 4000
    var i = 0
    while (i < message.length) {
        val end = (i + maxLogSize).coerceAtMost(message.length)
        println(message.substring(i, end))
        i += maxLogSize
    }
}