package com.tezov.tuucho.shared.sample._system

class Logger {

    fun println(value: String) {
        kotlin.io.println("::>> $value")
    }

}
