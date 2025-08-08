package com.tezov.tuucho.kmm._system

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContext
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class CoroutineScopes(
    override val database: CoroutineContextProtocol = object :
        CoroutineContext("Database", Dispatchers.IO) {},
    override val network: CoroutineContextProtocol = object :
        CoroutineContext("Network", Dispatchers.IO) {},
    override val parser: CoroutineContextProtocol = object :
        CoroutineContext("Parser", Dispatchers.Default) {},
    override val renderer: CoroutineContextProtocol = object :
        CoroutineContext("Renderer", Dispatchers.Default) {},
    override val navigation: CoroutineContextProtocol = object :
        CoroutineContext("Navigation", Dispatchers.Default) {},
    override val event: CoroutineContextProtocol = object :
        CoroutineContext("Event", Dispatchers.Default) {},
    override val useCase: CoroutineContextProtocol = object :
        CoroutineContext("UseCase", Dispatchers.Default) {},
    override val default: CoroutineContextProtocol = object :
        CoroutineContext("Default", Dispatchers.Default) {},
    override val main: CoroutineContextProtocol = object :
        CoroutineContext("Main", Dispatchers.Main) {},
    override val io: CoroutineContextProtocol = object :
        CoroutineContext("IO", Dispatchers.IO) {},
) : CoroutineScopesProtocol
