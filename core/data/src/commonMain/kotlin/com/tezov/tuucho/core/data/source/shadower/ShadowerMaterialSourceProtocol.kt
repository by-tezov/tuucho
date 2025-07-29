package com.tezov.tuucho.core.data.source.shadower

import kotlinx.serialization.json.JsonObject

interface ShadowerMaterialSourceProtocol {

    val isCancelled: Boolean

    suspend fun onStart(url:String, materialElement: JsonObject)

    suspend fun onNext(jsonObject: JsonObject)

    suspend fun onDone(): JsonObject?
}
