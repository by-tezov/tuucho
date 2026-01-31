package com.tezov.tuucho.core.domain.business.protocol

interface CoroutineScopesProtocol {
    val unconfined: CoroutineContextProtocol
    val default: CoroutineContextProtocol
    val main: CoroutineContextProtocol
    val io: CoroutineContextProtocol
}
