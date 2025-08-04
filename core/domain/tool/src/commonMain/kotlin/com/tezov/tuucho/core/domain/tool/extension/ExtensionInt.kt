package com.tezov.tuucho.core.domain.tool.extension


object ExtensionInt {

    fun Int.isEven() = this % 2 == 0

    fun <T> Int.onEven(block: (Int) -> T) = if (isEven()) {
        block(this)
    } else null

    fun Int.isOdd() = this % 2 == 1

    fun <T> Int.onOdd(block: (Int) -> T) = if (isOdd()) {
        block(this)
    } else null

    fun <T> Int.action(ifEven: (Int) -> T, ifOdd: (Int) -> T) = if (isEven()) {
        ifEven(this)
    } else {
        ifOdd(this)
    }
}