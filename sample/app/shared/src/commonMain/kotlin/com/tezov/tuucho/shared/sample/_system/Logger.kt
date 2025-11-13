package com.tezov.tuucho.shared.sample._system

import co.touchlab.kermit.Logger as Kermit

class Logger(
    private val exceptionVerbose: Boolean,
    private val tag: String = ""
) {

    fun withTag(tag: String) = Logger(exceptionVerbose, tag.full())

    private fun String.full() = "${this@Logger.tag}:$this"

    private fun String.fullWithPrefix() = "$|>${full()}"

    fun debug(tag: String = "", message: () -> Any) {
        Kermit.d(tag = tag.fullWithPrefix(), null, message = { "${message()}" })
    }

    fun warning(tag: String = "", message: () -> Any) {
        Kermit.w(tag = tag.fullWithPrefix(), null, message = { "${message()}" })
    }

    fun info(tag: String = "", message: () -> Any) {
        Kermit.i(tag = tag.fullWithPrefix(), null, message = { "${message()}" })
    }

    fun exception(tag: String = "", throwable: Throwable? = null, message: () -> Any) {
        if (exceptionVerbose) {
            Kermit.e(tag = tag.fullWithPrefix(), throwable, message = { "${message()}" })
        } else {
            Kermit.e(tag = tag.fullWithPrefix(), null, message = { "${message()}: $throwable" })
        }
    }

}
