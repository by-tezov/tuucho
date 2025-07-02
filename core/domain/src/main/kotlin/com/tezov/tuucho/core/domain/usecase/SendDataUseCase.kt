package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.serialization.json.JsonElement

class SendDataUseCase(private val repository: MaterialRepositoryProtocol) {

    suspend fun invoke(url: String, data: JsonElement) = repository.send(url, data)

}