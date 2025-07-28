package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.domain.protocol.SendDataMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonElement

class SendDataMaterialRepository(
    private val materialNetworkSource: MaterialNetworkSource
) : SendDataMaterialRepositoryProtocol {

    override suspend fun sendData(url: String, data: JsonElement): JsonElement? {
        return materialNetworkSource.send(url, data)
    }
}
