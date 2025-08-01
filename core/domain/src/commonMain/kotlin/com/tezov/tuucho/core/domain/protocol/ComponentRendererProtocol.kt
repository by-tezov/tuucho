package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonObject

interface ViewProtocol {

    fun update(jsonObject: JsonObject) {}
}

interface ComponentRendererProtocol {

    fun process(url:String, componentObject: JsonObject): ViewProtocol?
}