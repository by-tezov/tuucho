package com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol

import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer

internal interface GuardProtocol {

    suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean
}
