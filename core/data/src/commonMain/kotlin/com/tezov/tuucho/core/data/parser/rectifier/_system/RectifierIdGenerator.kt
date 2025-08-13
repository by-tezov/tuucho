package com.tezov.tuucho.core.data.parser.rectifier._system

import kotlin.uuid.Uuid

class RectifierIdGenerator {

    fun generate() = Uuid.Companion.random().toHexString()

}