package com.tezov.tuucho.core.data.parser._system

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class IdGenerator {

    @OptIn(ExperimentalUuidApi::class)
    fun generate() = Uuid.random().toHexString()

}