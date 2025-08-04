package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.serialization.json.JsonObject

interface ViewProtocol {

    fun update(jsonObject: JsonObject) {}
}