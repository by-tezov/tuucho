package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol

interface CoroutineScopesProtocol {
    val database: CoroutineContextProtocol
    val network: CoroutineContextProtocol
    val parser: CoroutineContextProtocol
    val renderer: CoroutineContextProtocol
    val navigation: CoroutineContextProtocol
    val useCase: CoroutineContextProtocol
    val action: CoroutineContextProtocol
    val event: CoroutineContextProtocol

    val default: CoroutineContextProtocol
    val main: CoroutineContextProtocol
    val io: CoroutineContextProtocol
}
