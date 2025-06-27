package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonObject

interface ScreenProtocol {
    fun display()
}

interface ScreenRendererProtocol {
    fun process(component: JsonObject): ScreenProtocol?
}