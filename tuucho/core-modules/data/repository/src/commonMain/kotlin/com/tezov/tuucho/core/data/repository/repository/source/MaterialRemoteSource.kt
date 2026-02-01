package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import kotlinx.serialization.json.JsonObject

internal class MaterialRemoteSource(
    private val remoteSource: RemoteSource,
    private val materialRectifier: MaterialRectifier,
) {
    suspend fun process(
        url: String
    ): JsonObject {
        val response = remoteSource.resource(url)
        return materialRectifier.process(
            context = RectifierProtocol.Context(
                url = url
            ),
            materialObject = response
        )
    }
}
