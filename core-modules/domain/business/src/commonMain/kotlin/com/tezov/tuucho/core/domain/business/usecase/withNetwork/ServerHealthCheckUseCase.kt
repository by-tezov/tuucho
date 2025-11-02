package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ServerHealthCheckProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.tool.json.string

class ServerHealthCheckUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val serverHealthCheck: ServerHealthCheckProtocol,
) : UseCaseProtocol.Async<ServerHealthCheckUseCase.Input, ServerHealthCheckUseCase.Output>,
    TuuchoKoinComponent {
    data class Input(
        val url: String,
    )

    data class Output(
        val status: String,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        val response = coroutineScopes.network.await {
            serverHealthCheck.process(url)
        }
        Output(status = response["health"].string)
    }
}
