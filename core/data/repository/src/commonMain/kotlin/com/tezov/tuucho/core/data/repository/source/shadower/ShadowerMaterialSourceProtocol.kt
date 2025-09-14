package com.tezov.tuucho.core.data.repository.source.shadower

import kotlinx.serialization.json.JsonObject

interface ShadowerMaterialSourceProtocol {

    val type: String

    val isCancelled: Boolean

    suspend fun onStart(url: String, materialElement: JsonObject)

    suspend fun onNext(jsonObject: JsonObject, settingObject: JsonObject?)

    suspend fun onDone(): List<JsonObject>
}
