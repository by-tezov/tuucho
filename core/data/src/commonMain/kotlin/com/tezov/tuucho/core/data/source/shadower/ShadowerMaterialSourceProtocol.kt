package com.tezov.tuucho.core.data.source.shadower

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface ShadowerMaterialSourceProtocol {

    val type: String

    val isCancelled: Boolean

    suspend fun onStart(url:String, materialElement: JsonObject)

    suspend fun onNext(jsonObject: JsonObject)

    suspend fun onDone(): Flow<JsonObject>
}
