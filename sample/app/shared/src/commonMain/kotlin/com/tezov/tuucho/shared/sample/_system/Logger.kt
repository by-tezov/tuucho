package com.tezov.tuucho.shared.sample._system

class Logger {

    fun println(value: Any) {
        kotlin.io.println("::>> $value")
    }

}
