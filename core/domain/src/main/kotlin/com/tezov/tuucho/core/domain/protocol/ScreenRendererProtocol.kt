package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonElement

interface ScreenProtocol

interface ScreenRendererProtocol {
    fun process(component: JsonElement): ScreenProtocol?
}