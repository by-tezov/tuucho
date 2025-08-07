package com.tezov.tuucho.core.presentation.ui.renderer._system

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class IdGenerator {

    @OptIn(ExperimentalUuidApi::class)
    fun generate() = Uuid.Companion.random().toHexString()

}