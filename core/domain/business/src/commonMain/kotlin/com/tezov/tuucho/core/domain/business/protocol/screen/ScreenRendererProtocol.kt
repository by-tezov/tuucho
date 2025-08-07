package com.tezov.tuucho.core.domain.business.protocol.screen

import kotlinx.serialization.json.JsonObject

interface ScreenRendererProtocol {

    suspend fun process(componentObject: JsonObject): ScreenProtocol
}