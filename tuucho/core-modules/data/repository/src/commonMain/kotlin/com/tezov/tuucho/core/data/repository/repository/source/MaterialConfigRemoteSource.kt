package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.parser.rectifier.material.config.ConfigRectifier
import kotlinx.serialization.json.JsonObject

internal class MaterialConfigRemoteSource(
    private val remoteSource: RemoteSource,
    private val configRectifier: ConfigRectifier,
) {
    suspend fun process(
        url: String
    ): JsonObject {
        val response = remoteSource.resource(url)
        return configRectifier.process(
            configObject = response
        )
    }
}
