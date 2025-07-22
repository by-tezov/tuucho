package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

class SendDataUseCase(
    private val coroutineDispatchers: CoroutineDispatchersProtocol,
    private val repository: MaterialRepositoryProtocol,
) {

    suspend fun invoke(url: String, data: JsonElement) = withContext(coroutineDispatchers.io) {
        repository.send(url, data)
    }

}