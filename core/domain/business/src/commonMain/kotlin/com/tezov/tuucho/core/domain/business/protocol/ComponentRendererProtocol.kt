package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.serialization.json.JsonObject

interface ComponentRendererProtocol {

    fun process(url: String, componentObject: JsonObject): ViewProtocol?
}