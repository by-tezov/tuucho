package com.tezov.tuucho.core.domain.business.protocol.screen.view

import kotlinx.serialization.json.JsonObject

interface ViewProtocol {

    suspend fun update(jsonObject: JsonObject)
}