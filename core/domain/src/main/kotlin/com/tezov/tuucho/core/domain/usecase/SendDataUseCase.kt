package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonObject

class SendDataUseCase(private val repository: MaterialRepositoryProtocol) {

    suspend fun invoke(url: String, data: JsonObject) = repository.send(url, data)

}