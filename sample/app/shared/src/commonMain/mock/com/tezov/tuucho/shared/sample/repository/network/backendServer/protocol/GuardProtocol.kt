package com.tezov.tuucho.shared.sample.repository.network.backendServer.protocol

import com.tezov.tuucho.shared.sample.repository.network.backendServer.BackendServer

internal interface GuardProtocol {

    suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean
}
