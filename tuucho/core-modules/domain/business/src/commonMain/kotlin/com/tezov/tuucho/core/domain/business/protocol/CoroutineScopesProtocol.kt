package com.tezov.tuucho.core.domain.business.protocol

interface CoroutineScopesProtocol {
    val unconfined: CoroutineScopeProtocol
    val default: CoroutineScopeProtocol
    val main: CoroutineScopeProtocol
    val io: CoroutineScopeProtocol

    fun cancel()
}
