package com.tezov.tuucho.core.data.parser._system

import kotlin.uuid.Uuid

object IdGenerator {

    fun generate() = Uuid.random().toHexString()

}