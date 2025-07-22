package com.tezov.tuucho.core.data.parser._system

import kotlin.uuid.Uuid

class IdGenerator {

    fun generate() = Uuid.random().toHexString()

}