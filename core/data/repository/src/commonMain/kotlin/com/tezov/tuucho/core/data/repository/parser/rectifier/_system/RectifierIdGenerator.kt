package com.tezov.tuucho.core.data.repository.parser.rectifier._system

import kotlin.uuid.Uuid

class RectifierIdGenerator {

    fun generate() = Uuid.Companion.random().toHexString()

}