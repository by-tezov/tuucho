package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonObject

interface ScreenProtocol {

    fun update(jsonObject: JsonObject) {}
}

interface ScreenRendererProtocol {

    fun process(url:String, componentElement: JsonObject): ScreenProtocol?
}