package com.tezov.tuucho.core.presentation.ui.renderer._system

import kotlin.uuid.Uuid

class IdGenerator {

    fun generate() = Uuid.Companion.random().toHexString()

}