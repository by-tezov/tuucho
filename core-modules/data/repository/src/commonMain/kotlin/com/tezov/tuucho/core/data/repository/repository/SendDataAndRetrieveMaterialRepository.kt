package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.repository.source.SendDataAndRetrieveMaterialRemoteSource
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class SendDataAndRetrieveMaterialRepository(
    private val sendObjectAndRetrieveMaterialRemoteSource: SendDataAndRetrieveMaterialRemoteSource
) : MaterialRepositoryProtocol.SendDataAndRetrieve {

    override suspend fun process(url: String, jsonObject: JsonObject): JsonObject? {
        return sendObjectAndRetrieveMaterialRemoteSource.process(url, jsonObject)
    }

}
