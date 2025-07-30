package com.tezov.tuucho.core.domain.protocol

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProviderProtocol {
    val network: CoroutineScope
    val database: CoroutineScope
    val parser: CoroutineScope
    val event: CoroutineScope
    val uiProcessor: CoroutineScope
}
