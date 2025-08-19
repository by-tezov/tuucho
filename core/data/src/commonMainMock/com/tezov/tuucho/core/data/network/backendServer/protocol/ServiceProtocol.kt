package com.tezov.tuucho.core.data.network.backendServer.protocol

import com.tezov.tuucho.core.data.network.backendServer.BackendServer.Request
import com.tezov.tuucho.core.data.network.backendServer.BackendServer.Response

interface ServiceProtocol {

    val url: String

    val version: String

    fun process(request: Request): Response

}