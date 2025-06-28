package com.tezov.tuucho.core.domain.protocol

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface CoroutineDispatchersProtocol {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class CoroutineDispatchersImpl: CoroutineDispatchersProtocol {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
}