package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.source.SendDataAndRetrieveMaterialRemoteSource
import com.tezov.tuucho.core.domain.protocol.SendDataAndRetrieveMaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class SendDataAndRetrieveMaterialRepository(
    private val sendObjectAndRetrieveMaterialRemoteSource: SendDataAndRetrieveMaterialRemoteSource
) : SendDataAndRetrieveMaterialRepositoryProtocol {

    override suspend fun process(url: String, data: JsonObject): JsonObject? {
        return sendObjectAndRetrieveMaterialRemoteSource.process(url, data)
    }
}
