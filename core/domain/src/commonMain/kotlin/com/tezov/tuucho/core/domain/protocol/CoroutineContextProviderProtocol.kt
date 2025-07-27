package com.tezov.tuucho.core.domain.protocol

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProviderProtocol {
    val main: CoroutineContext
    val io: CoroutineContext
    val default: CoroutineContext
}

class CoroutineContextProvider: CoroutineContextProviderProtocol { //TODO move somewhere else...
    override val main: CoroutineContext = Dispatchers.Main
    override val io: CoroutineContext = Dispatchers.IO
    override val default: CoroutineContext = Dispatchers.Default
}