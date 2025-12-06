package com.tezov.tuucho.shared.sample.repository.network.backendServer.protocol

import com.tezov.tuucho.shared.sample.repository.network.backendServer.BackendServer

internal interface ServiceProtocol {

    fun matches(url: String): Boolean

    suspend fun allowed(
        version: String,
        request: BackendServer.Request,
    ): Boolean

    suspend fun process(
        version: String,
        request: BackendServer.Request
    ): BackendServer.Response
}
