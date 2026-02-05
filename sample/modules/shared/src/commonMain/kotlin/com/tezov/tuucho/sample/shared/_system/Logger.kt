package com.tezov.tuucho.sample.shared._system

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import co.touchlab.kermit.Logger as Kermit

class Logger(
    private val systemInformation: SystemInformationProtocol,
    private val exceptionVerbose: Boolean,
    private val tag: String = ""
) {

    private val kermit = Kermit(
        config = StaticConfig(
            minSeverity = Severity.Verbose,
            logWriterList = listOf(CommonWriter())
//            logWriterList = listOf(platformLogWriter()) // seem to bug on ios cause I don't see the debug level
        )
    )

    fun withTag(tag: String) = Logger(systemInformation, exceptionVerbose, tag.full())

    private fun String.full() = "${this@Logger.tag}:$this"

    private fun String.fullWithPrefix() = "$|>${full()}"

    fun debug(tag: String = "", message: () -> Any) {
        kermit.d(tag = tag.fullWithPrefix(), throwable = null, message = { "${message()}" })
    }

    fun warning(tag: String = "", message: () -> Any) {
        kermit.w(tag = tag.fullWithPrefix(), throwable = null, message = { "${message()}" })
    }

    fun info(tag: String = "", message: () -> Any) {
        kermit.i(tag = tag.fullWithPrefix(), throwable = null, message = { "${message()}" })
    }

    fun exception(tag: String = "", throwable: Throwable? = null, message: () -> Any) {
        if (exceptionVerbose) {
            kermit.e(
                tag = tag.fullWithPrefix(),
                throwable = throwable,
                message = { "${message()}" })
        } else {
            kermit.e(
                tag = tag.fullWithPrefix(),
                throwable = null,
                message = { "${message()}: $throwable" })
        }
    }

    suspend fun thread() {
        val currentDispatcher = systemInformation.currentDispatcher()
        info("THREAD") { "${systemInformation.currentThreadName()} - $currentDispatcher" }
    }

}
