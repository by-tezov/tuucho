package com.tezov.tuucho.core.data.repository.repository.source.shadower

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

internal interface ShadowerMaterialSourceProtocol<C : ShadowerMaterialSourceProtocol.Context> {
    interface Context

    val type: String

    fun accept(
        url: String,
        setting: JsonObject?,
        componentObject: JsonObject
    ): Boolean

    fun createContext(
        url: String,
        setting: JsonObject?,
        componentObject: JsonObject
    ): C

    fun process(
        context: C,
        jsonObject: JsonObject
    )

    suspend fun finalize(
        context: C
    ): Flow<JsonObject>
}
